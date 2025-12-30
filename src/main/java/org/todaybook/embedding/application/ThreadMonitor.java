package org.todaybook.embedding.application;

import jakarta.annotation.PostConstruct;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("local")
public class ThreadMonitor {
  private ThreadMXBean threadMXBean;
  private Map<String, Integer> previous = new HashMap<>();

  @PostConstruct
  void init() {
    this.threadMXBean = ManagementFactory.getThreadMXBean();
  }

  @Scheduled(fixedDelay = 5000)
  public void logThreads() {
    int total = threadMXBean.getThreadCount();
    int peak = threadMXBean.getPeakThreadCount();

    Map<String, Integer> current = new HashMap<>();
    Map<String, Integer> elgGroups = new HashMap<>();
    Map<Thread.State, Integer> stateCount = new HashMap<>();

    for (long id : threadMXBean.getAllThreadIds()) {
      ThreadInfo info = threadMXBean.getThreadInfo(id);
      if (info == null) continue;

      String name = info.getThreadName();

      if (!isTargetThread(name)) continue;

      String key = normalize(name);
      current.merge(key, 1, Integer::sum);

      stateCount.merge(info.getThreadState(), 1, Integer::sum);

      String elg = extractElgGroup(name);
      if (elg != null) {
        elgGroups.merge(elg, 1, Integer::sum);
      }
    }

    log.warn(
        "[THREAD] total={}, peak={}, states={}, elgGroups={}", total, peak, stateCount, elgGroups);

    current.forEach(
        (name, count) -> {
          int prev = previous.getOrDefault(name, 0);
          if (count > prev) {
            log.error("[THREAD-INCREASE] {} : {} -> {} (+{})", name, prev, count, count - prev);
          }
        });

    previous.forEach(
        (name, prev) -> {
          int curr = current.getOrDefault(name, 0);
          if (curr < prev) {
            log.info("[THREAD-DECREASE] {} : {} -> {} (-{})", name, prev, curr, prev - curr);
          }
        });

    previous = current;
  }

  private String extractElgGroup(String name) {
    // grpc-nio-worker-ELG-6-1 → ELG-6
    int idx = name.indexOf("ELG-");
    if (idx < 0) return null;

    String tail = name.substring(idx); // ELG-6-1
    String[] parts = tail.split("-");
    return parts.length >= 2 ? "ELG-" + parts[1] : null;
  }

  private boolean isTargetThread(String name) {
    return name.contains("boundedElastic")
        || name.contains("grpc")
        || name.contains("gax")
        || name.contains("Netty");
  }

  private String normalize(String name) {
    // boundedElastic-123 → boundedElastic
    int idx = name.lastIndexOf('-');
    return idx > 0 ? name.substring(0, idx) : name;
  }
}

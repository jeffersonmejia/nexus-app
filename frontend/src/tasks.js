export function parseStoredValue(value, fallback) {
  if (value == null) return fallback;

  try {
    return JSON.parse(value) ?? fallback;
  } catch {
    return fallback;
  }
}

export function filterTasks(tasks, status, query) {
  const normalizedQuery = query.trim().toLowerCase();

  return tasks.filter((task) => {
    const matchesStatus = status === 'Todas'
      || (status === 'Pendientes' && !task.done)
      || (status === 'Completadas' && task.done);
    const searchableText = `${task.title} ${task.course}`.toLowerCase();

    return matchesStatus && searchableText.includes(normalizedQuery);
  });
}

export function getTaskStats(tasks, today) {
  return tasks.reduce((stats, task) => {
    if (task.done) {
      stats.completed += 1;
    } else {
      stats.pending += 1;
      if (task.date < today) stats.overdue += 1;
    }

    return stats;
  }, { pending: 0, completed: 0, overdue: 0 });
}

export function toggleTask(tasks, id) {
  return tasks.map((task) => task.id === id ? { ...task, done: !task.done } : task);
}

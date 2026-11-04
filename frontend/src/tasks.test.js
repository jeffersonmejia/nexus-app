import test from 'node:test';
import assert from 'node:assert/strict';
import { filterTasks, getTaskStats, parseStoredValue, toggleTask } from './tasks.js';

const tasks = [
  { id: 1, title: 'Ensayo de literatura', course: 'Literatura', date: '2026-11-03', done: false },
  { id: 2, title: 'Resolver ejercicios', course: 'Cálculo II', date: '2026-11-04', done: true },
  { id: 3, title: 'Leer sistemas distribuidos', course: 'Arquitectura', date: '2026-11-02', done: false },
];

test('parseStoredValue parses valid JSON', () => {
  assert.deepEqual(parseStoredValue('[1,2]', []), [1, 2]);
});

test('parseStoredValue returns fallback for malformed or null values', () => {
  const fallback = [];
  assert.equal(parseStoredValue('{bad json', fallback), fallback);
  assert.equal(parseStoredValue(null, fallback), fallback);
  assert.equal(parseStoredValue('null', fallback), fallback);
});

test('filterTasks filters by status and case-insensitive title/course query', () => {
  assert.deepEqual(filterTasks(tasks, 'Pendientes', '  LITERATURA '), [tasks[0]]);
  assert.deepEqual(filterTasks(tasks, 'Completadas', ''), [tasks[1]]);
  assert.deepEqual(filterTasks(tasks, 'Todas', 'sistemas'), [tasks[2]]);
});

test('getTaskStats counts pending, completed, and overdue tasks', () => {
  assert.deepEqual(getTaskStats(tasks, '2026-11-04'), {
    pending: 2,
    completed: 1,
    overdue: 2,
  });
});

test('toggleTask changes only the matching task and leaves input unchanged', () => {
  const result = toggleTask(tasks, 1);
  assert.equal(result[0].done, true);
  assert.equal(result[1], tasks[1]);
  assert.equal(tasks[0].done, false);
});

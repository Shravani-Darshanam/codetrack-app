import { useEffect, useState, useCallback } from 'react';
import { api, getToken, saveSession, clearSession } from './api.js';

function Login({ onLogin }) {
  const [form, setForm] = useState({ name: '', email: '', password: '' });
  const [mode, setMode] = useState('login');
  const [error, setError] = useState('');
  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value });
  const submit = async (e) => {
    e.preventDefault(); setError('');
    try {
      const r = await api('/auth/' + mode, 'POST', form);
      saveSession(r.token, r.name); onLogin();
    } catch (err) { setError(err.message); }
  };
  return (
    <form className="card" onSubmit={submit}>
      <h2>{mode === 'login' ? 'Login' : 'Create account'}</h2>
      {mode === 'register' && <input placeholder="Name" value={form.name} onChange={set('name')} required />}
      <input type="email" placeholder="Email" value={form.email} onChange={set('email')} required />
      <input type="password" placeholder="Password (min 6)" value={form.password} onChange={set('password')} required minLength={6} />
      {error && <p className="err">{error}</p>}
      <button type="submit">{mode === 'login' ? 'Login' : 'Register'}</button>
      <button type="button" className="link" onClick={() => setMode(mode === 'login' ? 'register' : 'login')}>
        {mode === 'login' ? 'New here? Register' : 'Have an account? Login'}
      </button>
    </form>
  );
}

function Progress({ stats }) {
  if (!stats) return null;
  const pct = Math.min(100, (stats.today * 100) / stats.target);
  const max = Math.max(1, ...Object.values(stats.last7));
  return (
    <div className="card">
      <b>Today: {stats.today} / {stats.target}</b>
      <div className="bar"><i style={{ width: pct + '%' }} /></div>
      <small>🔥 Streak: {stats.streak} day(s) · Total solved: {stats.total}</small>
      <div className="week">
        {Object.entries(stats.last7).map(([d, n]) => (
          <div key={d} title={d + ': ' + n}><span style={{ height: (n * 40) / max + 2 }} /><small>{d.slice(8)}</small></div>
        ))}
      </div>
    </div>
  );
}

const empty = { platform: 'LeetCode', difficulty: 'Easy', title: '', url: '', tags: '', notes: '' };
function AddProblem({ onAdded }) {
  const [f, setF] = useState(empty);
  const set = (k) => (e) => setF({ ...f, [k]: e.target.value });
  const submit = async (e) => { e.preventDefault(); await api('/problems', 'POST', f); setF(empty); onAdded(); };
  return (
    <form className="card" onSubmit={submit}>
      <b>Add solved problem</b>
      <div className="row">
        <select value={f.platform} onChange={set('platform')}>{['LeetCode', 'Codeforces', 'HackerRank', 'CodeChef', 'GeeksforGeeks', 'Other'].map((p) => <option key={p}>{p}</option>)}</select>
        <select value={f.difficulty} onChange={set('difficulty')}>{['Easy', 'Medium', 'Hard'].map((p) => <option key={p}>{p}</option>)}</select>
      </div>
      <input placeholder="Problem title" value={f.title} onChange={set('title')} required />
      <input placeholder="Problem link" value={f.url} onChange={set('url')} />
      <input placeholder="Tags (arrays, dp...)" value={f.tags} onChange={set('tags')} />
      <textarea rows={2} placeholder="Your approach / key idea" value={f.notes} onChange={set('notes')} />
      <button type="submit">Save</button>
    </form>
  );
}

function ProblemList({ problems, onDelete }) {
  return (
    <div className="card">
      <b>Solved problems</b>
      {problems.length === 0 && <p><small>Nothing yet. Add your first one!</small></p>}
      {problems.map((p) => (
        <div className="item" key={p.id}>
          <div>
            {p.url ? <a href={p.url} target="_blank" rel="noreferrer">{p.title}</a> : p.title}{' '}
            <small>{p.platform} · {p.difficulty} · {p.solvedDate}</small>
            {(p.tags || p.notes) && <div><small>{p.tags} {p.notes}</small></div>}
          </div>
          <button className="link" onClick={() => onDelete(p.id)}>✕</button>
        </div>
      ))}
    </div>
  );
}

function WeeklyTest({ onDone }) {
  const [test, setTest] = useState(null);
  const [msg, setMsg] = useState('');
  const [ratings, setRatings] = useState({});
  const load = async () => {
    try { const t = await api('/tests/current'); setTest(t); setMsg(''); setRatings({}); }
    catch (e) { setTest(null); setMsg(e.message); }
  };
  const submit = async () => {
    const answers = test.questions.map((q) => ({ questionId: q.id, rating: Number(ratings[q.id] || 1) }));
    setTest(await api('/tests/' + test.id + '/submit', 'POST', answers)); onDone();
  };
  const done = test?.status === 'COMPLETED';
  return (
    <div className="card">
      <div className="row"><b>Weekly test</b><button onClick={load}>{test ? 'Reload' : 'Load this week\'s test'}</button></div>
      {msg && <small>{msg}</small>}
      {done && <p><b>Score: {test.score}%</b></p>}
      {test?.questions.map((q) => (
        <div className="item" key={q.id}>
          <div>{q.url ? <a href={q.url} target="_blank" rel="noreferrer">{q.title}</a> : q.title} <small>{q.platform}</small></div>
          <select disabled={done} value={done ? q.rating : ratings[q.id] || 1} onChange={(e) => setRatings({ ...ratings, [q.id]: e.target.value })}>
            <option value={1}>Forgot</option><option value={2}>Partly</option><option value={3}>Remembered</option>
          </select>
        </div>
      ))}
      {test && !done && <button onClick={submit}>Submit test</button>}
    </div>
  );
}

function Dashboard({ onLogout }) {
  const [stats, setStats] = useState(null);
  const [problems, setProblems] = useState([]);
  const refresh = useCallback(async () => {
    setStats(await api('/stats')); setProblems(await api('/problems'));
  }, []);
  useEffect(() => { refresh(); }, [refresh]);
  const del = async (id) => { await api('/problems/' + id, 'DELETE'); refresh(); };
  return (
    <>
      <div className="row"><h2>Hi, {localStorage.getItem('name')}</h2><button className="link" onClick={onLogout}>Logout</button></div>
      <Progress stats={stats} />
      <AddProblem onAdded={refresh} />
      <WeeklyTest onDone={refresh} />
      <ProblemList problems={problems} onDelete={del} />
    </>
  );
}

export default function App() {
  const [loggedIn, setLoggedIn] = useState(!!getToken());
  return (
    <main>
      <h1>CodeTrack</h1>
      {loggedIn ? <Dashboard onLogout={() => { clearSession(); setLoggedIn(false); }} /> : <Login onLogin={() => setLoggedIn(true)} />}
    </main>
  );
}

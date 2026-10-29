export const getToken = () => localStorage.getItem('token');
export const saveSession = (token, name) => { localStorage.setItem('token', token); localStorage.setItem('name', name); };
export const clearSession = () => localStorage.clear();

export async function api(path, method = 'GET', body) {
  const token = getToken();
  const res = await fetch('/api' + path, {
    method,
    headers: { 'Content-Type': 'application/json', ...(token ? { Authorization: 'Bearer ' + token } : {}) },
    body: body ? JSON.stringify(body) : undefined,
  });
  const text = await res.text();
  const data = text ? JSON.parse(text) : null;
  if (res.status === 401 && token) { clearSession(); window.location.reload(); }
  if (!res.ok) throw new Error(data?.error || 'Request failed (' + res.status + ')');
  return data;
}

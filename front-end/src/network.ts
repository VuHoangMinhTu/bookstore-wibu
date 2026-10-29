const isLocal = [
    'localhost',
    '127.0.0.1'
].includes(window.location.hostname)
const listUrlAdminAllowedHosts = [
    // "back-end.minhtu.site",
    "api.wibubook.online"
]
const BACKEND_URL = isLocal ? 'http://localhost:8080' : 'https://api.wibubook.online'
export const API_BASE_URL = BACKEND_URL
export const WS_BASE_URL = isLocal ? 'ws://localhost:8080' : 'wss://api.wibubook.online'
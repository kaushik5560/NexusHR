import http from 'k6/http';
import { check, sleep } from 'k6';

// Non-functional requirement from Zidio PDF:
// Throughput: Handle 10k+ concurrent HR users during payroll cycles
// Latency: < 300 ms for core API calls, < 2 s for dashboard loads
export const options = {
  stages: [
    { duration: '30s', target: 50 },    // Ramp-up to 50 virtual users
    { duration: '1m', target: 200 },     // Sustained load test
    { duration: '30s', target: 500 },    // Peak load burst test
    { duration: '30s', target: 0 },      // Ramp-down
  ],
  thresholds: {
    http_req_duration: ['p(95)<300'],   // 95% of requests must complete below 300ms
    http_req_failed: ['rate<0.01'],      // Error rate must be less than 1%
  },
};

const BASE_URL = __ENV.API_BASE_URL || 'http://localhost:8080/api';

export default function () {
  // 1. Employee Directory Lookup
  const empRes = http.get(`${BASE_URL}/employees`);
  check(empRes, {
    'employees status is 200 or 401': (r) => r.status === 200 || r.status === 401,
    'latency is below 300ms': (r) => r.timings.duration < 300,
  });

  // 2. AI Attrition Overview
  const aiRes = http.get(`${BASE_URL}/ai/attrition/overview`);
  check(aiRes, {
    'ai overview responsive': (r) => r.status === 200 || r.status === 401,
  });

  sleep(1);
}

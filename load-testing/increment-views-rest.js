import http from 'k6/http';
import { check } from 'k6';
import { group } from 'k6';

export const options = {
  discardResponseBodies: true,
  scenarios: {
    contacts: {
      executor: 'constant-arrival-rate',
      // How long the test lasts
      duration: '5s',
      // How many iterations per timeUnit
      rate: 100,
      // Start `rate` iterations per second
      timeUnit: '1s',
      // Pre-allocate 2 VUs before starting the test
      preAllocatedVUs: 2,
      // Spin up a maximum of 50 VUs to sustain the defined
      // constant arrival rate.
      maxVUs: 50,
    },
  },
};

export default function () {
  group('Increment views', function () {
          const url = 'http://localhost:8085/api/v1/advertisements/66fe85f2cf73fc03be83a38c:viewed';
          const payload = JSON.stringify({
              // Include any necessary payload here if needed
          });

          const params = {
              headers: {
                  'Content-Type': 'application/json',
              },
          };

          let response = http.post(url, payload, params);

          // Check that the response status is 200
          check(response, {
              'is status 200': (r) => r.status === 200,
          });
      });
}
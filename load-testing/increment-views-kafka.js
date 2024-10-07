import { check } from 'k6';
import {
  Writer,
  Reader,
  Connection,
  SchemaRegistry,
  SCHEMA_TYPE_STRING,
} from "k6/x/kafka"; // import kafka extension

const bootstrapServers = ['localhost:29092'];
const kafkaTopic = 'advertisement-views';

const producer = new Writer({
  brokers: bootstrapServers,
  topic: kafkaTopic
});
const schemaRegistry = new SchemaRegistry();

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
// for (let index = 0; index < 100; index++) {
  const messages = [
        {
          key: schemaRegistry.serialize({
            data: "66fe85f2cf73fc03be83a38c",
            schemaType: SCHEMA_TYPE_STRING,
          }),
          value: schemaRegistry.serialize({
            data: "1",
            schemaType: SCHEMA_TYPE_STRING,
          })
        }
  ];

  const error = producer.produce({ messages: messages });
  check(error, {
    'is sent': (err) => err == undefined,
  });
 }
//}

export function teardown(data) {
  producer.close();
}
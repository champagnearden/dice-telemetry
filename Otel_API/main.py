#!/usr/bin/env python3
"""
OTLP HTTP receiver that listens on port 4318,
parses incoming OTLP trace, log, and metric data,
and appends each request as one JSON object per line
to traces.jsonl, logs.jsonl, and metrics.jsonl respectively.
"""
from http.server import BaseHTTPRequestHandler, HTTPServer
import logging
import json
from google.protobuf.json_format import MessageToDict

# Protobuf definitions
from opentelemetry.proto.collector.trace.v1.trace_service_pb2 import ExportTraceServiceRequest
from opentelemetry.proto.collector.logs.v1.logs_service_pb2 import ExportLogsServiceRequest
from opentelemetry.proto.collector.metrics.v1.metrics_service_pb2 import ExportMetricsServiceRequest

logging.basicConfig(level=logging.DEBUG, format='%(asctime)s %(message)s')

class OTLPHandler(BaseHTTPRequestHandler):
    def _write_jsonl(self, filename: str, message) -> None:
        # Convert protobuf message to a Python dict, then to a compact JSON line
        record = MessageToDict(message, preserving_proto_field_name=True)
        with open(filename, 'a', encoding='utf-8') as f:
            f.write(json.dumps(record))
            f.write('\n')

    def do_POST(self):
        length = int(self.headers.get('Content-Length', 0))
        data = self.rfile.read(length)
        path = self.path.lower()

        try:
            if path.endswith("/v1/traces"):
                req = ExportTraceServiceRequest()
                req.ParseFromString(data)
                logging.info("Received %d spans", len(req.resource_spans))
                self._write_jsonl('traces.jsonl', req)

            elif path.endswith("/v1/logs"):
                req = ExportLogsServiceRequest()
                req.ParseFromString(data)
                logging.info("Received %d log records", len(req.resource_logs))
                self._write_jsonl('logs.jsonl', req)

            elif path.endswith("/v1/metrics"):
                req = ExportMetricsServiceRequest()
                req.ParseFromString(data)
                logging.info("Received %d metric points", sum(len(rs.scope_metrics) for rs in req.resource_metrics))
                self._write_jsonl('metrics.jsonl', req)

            else:
                logging.warning("Unknown path %s, saving raw hex", path)
                with open('unknown.jsonl', 'a') as f:
                    f.write(json.dumps({
                        'path': path,
                        'body_hex': data.hex()
                    }))
                    f.write('\n')

        except Exception as e:
            logging.error("Failed to parse %s request: %s", path, e)
            logging.debug(type(req))

        # Respond 200 OK
        self.send_response(200)
        self.send_header('Content-Type', 'application/json')
        self.end_headers()
        self.wfile.write(b'{"message":"OK"}')

if __name__ == '__main__':
    port = 4000
    server = HTTPServer(('0.0.0.0', port), OTLPHandler)
    logging.info(f"OTLP HTTP server listening on port {port}")
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        logging.info("Shutting down server")
        server.server_close()

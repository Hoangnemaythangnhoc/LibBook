package com.example.libbook.entity;

public class ResponseWrapper {
        private boolean success;
        private String error;

        public ResponseWrapper(boolean success, String error) {
            this.success = success;
            this.error = error;
        }

        public boolean isSuccess() { return success; }
        public String getError() { return error; }
}

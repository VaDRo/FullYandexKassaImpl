package com.carwasher.testyanexkassa.enums;

public enum ErrorCode {
    io,
    inet_missing,
    need_non_ui_thread,
    checkout_canseled,
    need_permission;

    private ErrorCode() {
    }
}

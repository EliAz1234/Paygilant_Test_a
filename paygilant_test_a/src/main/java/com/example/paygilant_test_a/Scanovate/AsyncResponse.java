package com.example.paygilant_test_a.Scanovate;

public interface AsyncResponse {
    void onVerificationFinish(double verificationScore, double threshold);
    void onVerificationError(String error);
}
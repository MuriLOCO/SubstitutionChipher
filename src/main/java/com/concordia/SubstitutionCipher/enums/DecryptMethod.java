package com.concordia.SubstitutionCipher.enums;

public enum DecryptMethod {

  FAST_METHOD("Fast Method"), DECRYPT_AND_EVALUATE_METHOD("Decrypt and Evaluate Method"), MIXED_METHOD("Mixed Method");

  private final String value;

  private DecryptMethod(final String newValue) {
    value = newValue;
  }

  public String getValue() {
    return value;
  }
}

package com.concordia.SubstitutionCipher.enums;

public enum BaseVariables {

  //This is the English Alphabet defined in a Z26 (Upper case)
  ALPHABET_ENGLISH("ABCDEFGHIJKLMNOPQRSTUVWXYZ");

  private final String value;

  private BaseVariables(final String baseVariable) {
    value = baseVariable;
  }

  public String getValue() {
    return value;
  }
}

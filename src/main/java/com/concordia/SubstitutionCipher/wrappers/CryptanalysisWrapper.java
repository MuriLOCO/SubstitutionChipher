package com.concordia.SubstitutionCipher.wrappers;

import lombok.Data;

@Data
public class CryptanalysisWrapper {

  public CryptanalysisWrapper(final String key, final String text, final String notOrderedKey) {
    this.key = key;
    this.text = text;
    this.notOrderedKey = notOrderedKey;
  }

  private String key;
  private String text;
  private String notOrderedKey;

}

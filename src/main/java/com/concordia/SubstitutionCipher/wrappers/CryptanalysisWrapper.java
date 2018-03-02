package com.concordia.SubstitutionCipher.wrappers;

import lombok.Data;

@Data
public class CryptanalysisWrapper {

  public CryptanalysisWrapper(final String key, final String text){
    this.key = key;
    this.text = text;
  }
  
  private String key;
  private String text;
  
}

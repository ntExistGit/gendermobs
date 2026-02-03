package com.ntexist.gendermobs.accessor;

public interface LivingEntityAccessor {
    void setGender(String gender);
    String getGender();
    void setMobName(String name);
    String getMobName();
    String getOriginalId();
    void setOriginalId(String id);
}
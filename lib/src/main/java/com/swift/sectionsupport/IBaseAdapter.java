package com.swift.sectionsupport;

public interface IBaseAdapter {

    boolean isPrivatePosition(int position);

    int getInnerPosition(int position);

    int getOuterPosition(int position);

}

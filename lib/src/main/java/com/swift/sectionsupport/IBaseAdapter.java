package com.swift.sectionsupport;

public interface IBaseAdapter {

    //是否是该 Adapter 的内部 position
    boolean isPrivatePosition(int position);
    //由本层 Adapter 的 position 获取内层 Adapter 的 position
    int getInnerPosition(int position);
    //由内层 Adapter 的 position 获取本层 Adapter 的 position
    int getOuterPosition(int position);

}

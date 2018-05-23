package com.swift.sectionsupport;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by swift_gan on 2018/3/13.
 */

public class SectionSupportAdapter extends RecyclerView.Adapter implements IBaseAdapter {

    public final static int ITEM_VIEW_TYPE_SECTION = -100;
    public final static int ITEM_VIEW_TYPE_SECTION_HIDE = -101;

    private RecyclerView.Adapter adapter;
    private SectionSupport sectionSupport;

    private LinkedHashMap<Object, Integer> sections;
    private Map<Integer, Object> sectionPoints = new LinkedHashMap<>();

    private Map<Object,Boolean> isHides = new HashMap<>();

    private Map<Object,SectionViewHolder> sectionViewHolders = new HashMap<>();

    final RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            findSections();
            notifyDataSetChanged();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            int beforeEnd = getOuterPosition(positionStart + itemCount);
            findSections();
            int afterStart = getOuterPosition(positionStart);
            int trueItemCount = beforeEnd - afterStart;
            notifyItemRangeRemoved(afterStart, trueItemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            int trueItemStart = getOuterPosition(positionStart);
            findSections();
            int trueItemCount = getOuterPosition(positionStart + itemCount) - trueItemStart;
            notifyItemRangeChanged(trueItemStart, trueItemCount);
        }


        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            int beforeStart = getOuterPosition(positionStart);
            Object oldItemType = sectionPoints.get(positionStart);
            findSections();
            int afterStart = getOuterPosition(positionStart);
            int trueItemCount = getOuterPosition(positionStart + itemCount) - beforeStart;
            Object newItemType = sectionPoints.get(positionStart);
            if (newItemType != null && oldItemType != null && newItemType != oldItemType) {
                notifyItemRangeInserted(beforeStart - 1, trueItemCount + 1);
            } else {
                notifyItemRangeInserted(beforeStart, trueItemCount);
                Object firstType = getRawItemType(positionStart);
                if (firstType != null && beforeStart == afterStart) {
                    refreshSection(firstType);
                }
            }
        }
    };

    public SectionSupportAdapter() {
    }

    public SectionSupportAdapter(RecyclerView.Adapter adapter, SectionSupport sectionSupport) {
        this.adapter = adapter;
        this.sectionSupport = sectionSupport;
        init();
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        this.adapter = adapter;
        if (sectionSupport != null) {
            init();
        }
    }

    public void setSectionSupport(SectionSupport sectionSupport) {
        this.sectionSupport = sectionSupport;
        if (adapter != null) {
            init();
        }
    }

    private void init() {
        sections = new LinkedHashMap<>();
        adapter.registerAdapterDataObserver(observer);
        findSections();
    }

    public void destroy() {
        adapter.unregisterAdapterDataObserver(observer);
    }

    @Override
    public int getItemViewType(int position) {
        if (isPrivatePosition(position)) {
            return ITEM_VIEW_TYPE_SECTION;
        } else {
            int innerPosition = getInnerPosition(position);
            Boolean isHide = isHides.get(getRawItemType(innerPosition));
            if (isHide != null && isHide) {
                return ITEM_VIEW_TYPE_SECTION_HIDE;
            } else {
                return adapter.getItemViewType(getInnerPosition(position));
            }
        }
    }

    public boolean toggle(Object itemType) {
        if (isHides.containsKey(itemType)) {
            isHides.put(itemType, !isHides.get(itemType));
        } else {
            isHides.put(itemType, true);
        }
        adapter.notifyDataSetChanged();
        return isHides.get(itemType);
    }

    public void collapse(Object itemType) {
        hide(itemType, true);
    }

    public void expand(Object itemType) {
        hide(itemType, false);
    }

    public void hide(Object itemType, boolean hide) {
        isHides.put(itemType, hide);
        adapter.notifyDataSetChanged();
    }

    public void hideWithoutRefresh(Object itemType, boolean hide) {
        isHides.put(itemType, hide);
    }

    public boolean isExpanded(Object itemType) {
        if (isHides.containsKey(itemType))
            return !isHides.get(itemType);
        return true;
    }

    @UiThread
    public void refreshSection(Object itemType) {
        SectionViewHolder viewHolder = sectionViewHolders.get(itemType);
        if (viewHolder == null) {
            adapter.notifyDataSetChanged();
        } else {
            sectionSupport.setSectionTitle(itemType, viewHolder.itemView);
        }
    }

    private void setVisibility(RecyclerView.ViewHolder viewHolder, boolean isVisible){
        View itemView = viewHolder.itemView;
        if (isVisible && itemView.getVisibility() == View.VISIBLE) {
            return;
        }
        if (!isVisible && itemView.getVisibility() == View.GONE) {
            return;
        }
        ViewGroup.LayoutParams param = itemView.getLayoutParams();
        if (param == null) {
            param = new ViewGroup.LayoutParams(0, 0);
        }
        if (isVisible){
            param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            param.width = LinearLayout.LayoutParams.MATCH_PARENT;
            itemView.setVisibility(View.VISIBLE);
        }else{
            itemView.setVisibility(View.GONE);
            param.height = 0;
            param.width = 0;
        }
        itemView.setLayoutParams(param);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_VIEW_TYPE_SECTION) {
            return new SectionViewHolder(LayoutInflater.from(parent.getContext()).inflate(sectionSupport.sectionHeaderLayoutId(), parent, false));
        } else if (viewType == ITEM_VIEW_TYPE_SECTION_HIDE){
            return new NoneViewHolder(parent.getContext());
        } else {
            return adapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SectionViewHolder) {
            Object itemType = getItemType(position);
            if (itemType != null) {
                sectionViewHolders.put(itemType, (SectionViewHolder) holder);
                sectionSupport.setSectionTitle(itemType, holder.itemView);
            }
        } else if (holder instanceof NoneViewHolder) {
            return;
        } else {
            int innerPosition = getInnerPosition(position);
            adapter.onBindViewHolder(holder, innerPosition);
        }
    }

    @Override
    public int getItemCount() {
        return adapter.getItemCount() + sections.size();
    }

    public RecyclerView.Adapter getAdapter() {
        return adapter;
    }

    @Override
    public boolean isPrivatePosition(int position) {
        return sections.values().contains(position);
    }

    @Override
    public int getInnerPosition(int position) {
        int nSections = 0;
        Set<Map.Entry<Object, Integer>> entrySet = sections.entrySet();
        for (Map.Entry<Object, Integer> entry : entrySet) {
            if (entry.getValue() < position) {
                nSections++;
            }
        }
        return position - nSections;
    }

    @Override
    public int getOuterPosition(int position) {
        int nSections = 0;
        for (Map.Entry<Integer, Object> entry : sectionPoints.entrySet()) {
            if (position >= entry.getKey()) {
                nSections++;
            } else {
                break;
            }
        }
        return position + nSections;
    }

    public void findSections() {
        int n = adapter.getItemCount();
        int nSections = 0;
        sections.clear();
        sectionPoints.clear();
        for (int i = 0; i < n; i++) {
            Object itemType = sectionSupport.itemType(i);
            if (!sections.containsKey(itemType)) {
                sections.put(itemType, i + nSections);
                sectionPoints.put(i, itemType);
                nSections++;
            }
        }
    }

    public Object getItemType(int position) {
        for (Map.Entry<Object, Integer> entry : sections.entrySet()) {
            if (entry.getValue() == position) {
                return entry.getKey();
            }
        }
        return null;
    }

    public Object getRawItemType(int position) {
        Object type = null;
        for (Integer p:sectionPoints.keySet()) {
            if (position < p) {
                break;
            }
            if (sectionPoints.containsKey(p)) {
                type = sectionPoints.get(p);
            }
        }
        return type;
    }

    public class SectionViewHolder extends RecyclerView.ViewHolder {

        public SectionViewHolder(View itemView) {
            super(itemView);
        }

    }

    public class NoneViewHolder extends RecyclerView.ViewHolder {

        public NoneViewHolder(Context context) {
            super(new View(context));
            setVisibility(this, false);
        }

    }

    public interface SectionSupport {
        @LayoutRes
        int sectionHeaderLayoutId();

        Object itemType(int position);

        void setSectionTitle(Object itemType, View sectionView);
    }
}

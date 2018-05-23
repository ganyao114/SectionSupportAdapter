# SectionSupportAdapter
1. 支持分组  
2. 支持分组的收缩展开
3. 支持 插入/删除 动画
# Demo
```java
 sectionSupportAdapter = new SectionSupportAdapter(testAdapter, new SectionSupportAdapter.SectionSupport() {
            @Override
            public int sectionHeaderLayoutId() {
                return R.layout.item_section;
            }

            @Override
            public Object itemType(int position) {
                return testAdapter.getSection(position);
            }

            @Override
            public void setSectionTitle(final Object itemType, View sectionView) {
                sectionView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sectionSupportAdapter.toggle(itemType);
                    }
                });
                TextView textView = sectionView.findViewById(R.id.text);
                textView.setText(itemType.toString());
            }
        });
```
```java
//切换某分组的收缩状态
sectionSupportAdapter.toggle(itemType);
//收缩某分组
sectionSupportAdapter.collapse(itemType);
//展开某分组
sectionSupportAdapter.expand(itemType);
```
当 Adapter 责任链较多时需要暴露给其他 Adapter 的信息
```java
public interface IBaseAdapter {

    //是否是该 Adapter 的内部 position
    boolean isPrivatePosition(int position);
    //由本层 Adapter 的 position 获取内层 Adapter 的 position
    int getInnerPosition(int position);
    //由内层 Adapter 的 position 获取本层 Adapter 的 position
    int getOuterPosition(int position);

}
```
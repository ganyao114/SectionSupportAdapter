package sectionsupport.swift.com.sectionsupportadapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by swift_gan on 2018/5/23.
 */

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.TestViewHolder> {

    public String[] data = new String[]{"A1","A2","A3","A4","A5","B1","B2","B4","B3","C1","C2","C3","D4","D1","D2","D3","D4"};

    @Override
    public TestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TestViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_demo, parent, false));
    }

    @Override
    public void onBindViewHolder(TestViewHolder holder, int position) {
        holder.textView.setText(data[position]);
    }

    public String getSection(int position) {
        return data[position].substring(0, 1);
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public class TestViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public TestViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text);
        }

    }

}

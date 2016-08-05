package com.example.q.swipelayout;

import static com.example.q.swipelayout.Cheeses.names;//静态导入

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by YQ on 2016/8/5.
 */
public class MyAdapter extends BaseAdapter {
    private final ArrayList<SwipeLayout> openItems;
    private Context context;

    public MyAdapter(Context context) {
        this.context = context;
        openItems = new ArrayList<SwipeLayout>();
    }

    @Override
    public int getCount() {
        return names.length;
    }

    @Override
    public Object getItem(int n) {
        return names[n];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View root = view;
        if (view == null) {
            root = View.inflate(context, R.layout.item, null);
        }
        ViewHolder mHolder = ViewHolder.getHolder(root);
        mHolder.tv_name.setText(names[i]);
        SwipeLayout layout = (SwipeLayout) root;
        layout.setListener(new SwipeLayout.OnSwipeLayoutListener() {
            @Override
            public void onClose(SwipeLayout mSwipeLayout) {
                openItems.remove(mSwipeLayout);
            }

            @Override
            public void onOpen(SwipeLayout mSwipeLayout) {
                //添加进集合
                openItems.add(mSwipeLayout);
            }

            @Override
            public void onDragging(SwipeLayout mSwipeLayout) {

            }

            @Override
            public void onStartClose(SwipeLayout mSwipeLayout) {

            }

            @Override
            public void onStartOpen(SwipeLayout mSwipeLayout) {
                //要去开始时，先遍历所有已打开条目，逐个关闭
                for (SwipeLayout layout:openItems){
                    layout.close(true);
                }
            }
        });
        return root;
    }

    static class ViewHolder {
        TextView tv_call;
        TextView tv_del;
        TextView tv_name;

        public static ViewHolder getHolder(View view) {
            Object tag = view.getTag();
            if (tag == null) {
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.tv_call = (TextView) view.findViewById(R.id.tv_call);
                viewHolder.tv_del = (TextView) view.findViewById(R.id.tv_delete);
                viewHolder.tv_name = (TextView) view.findViewById(R.id.tv_name);
                tag = viewHolder;
                view.setTag(tag);
            }
            return (ViewHolder) tag;
        }
    }
}

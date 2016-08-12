# SwipeLayout
## 与侧滑面板不同在于：SwipeLayout是两个ViewGroup都在动，侧滑面板只有主面板
* ViewDragHelper:(两个面板都可以动，offsetLeftAndRight)
```Java
@Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                //边界限制
                if (child == mFrontView) {
                    if (left > 0) {
                        return 0;
                    } else if (left < -mRange) {
                        return -mRange;
                    }
                } else if (child == mBackView) {
                    if (left > mWidth) {
                        return mWidth;
                    } else if (left < mWidth - mRange) {
                        return mWidth - mRange;
                    }
                }
                return left;
            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                super.onViewPositionChanged(changedView, left, top, dx, dy);
                //传递偏移
                if (changedView == mFrontView) {
                    mBackView.offsetLeftAndRight(dx);
                } else if (changedView == mBackView) {
                    mFrontView.offsetLeftAndRight(dx);
                }
                dispatchSwipeEvent();
                invalidate();
            }
```
* onLayout:初始状态
```Java
//isOpen=false
private void layoutContent(boolean isOpen) {
        //摆放前View
        Rect frontRect = computeFrontViewRect(isOpen);
        mFrontView.layout(frontRect.left, frontRect.top, frontRect.right, frontRect.bottom);
        //根据前View，摆放后View
        Rect backRect = computeBackViewViaFront(frontRect);
        mBackView.layout(backRect.left, backRect.top, backRect.right, backRect.bottom);

    }
    private Rect computeBackViewViaFront(Rect frontRect) {
        int left = frontRect.right;
        return new Rect(left, 0, left + mRange, mHeight);
    }
    
    private Rect computeFrontViewRect(boolean isOpen) {
        int left = 0;
        if (isOpen) {
            left = -mRange;
        }
        return new Rect(left, 0, left + mWidth, mHeight);
    }
```

> 主要是Rect来实现测量

* 如果Item用了View.inflate(...)...，可以在XML通过minHeight设置一个最小高度，类似inflate（root，false）

## ViewHolder的另外一种写法：
```Java
   public View getView(int i, View view, ViewGroup viewGroup) {
        View root = view;
        if (view == null) {
            root = View.inflate(context, R.layout.item, null);
        }
        ViewHolder mHolder = ViewHolder.getHolder(root);
        mHolder.tv_name.setText(names[i]);
        ....
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
```
## 剩下的都是辣鸡

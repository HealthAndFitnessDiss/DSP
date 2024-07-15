package com.example.healthandfitness;
import android.content.Context;
import android.util.AttributeSet;
import androidx.cardview.widget.CardView;

public class CustomCardView extends CardView {

    public CustomCardView(Context context) {
        super(context);
        init();
    }

    public CustomCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // Set the properties for rounded corners and elevation
        setRadius(48f); // 16dp corner radius
        setCardElevation(0f); // 8dp elevation
        setCardBackgroundColor(getResources().getColor(android.R.color.white)); // White background color
    }
}

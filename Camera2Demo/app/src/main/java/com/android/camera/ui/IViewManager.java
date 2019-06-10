package com.android.camera.ui;

public interface IViewManager {
    /**
     * Set the visible state of this view.
     *
     * @param visibility One of {@link #android.view.View.VISIBLE},
     * {@link #android.view.View.INVISIBLE}, or {@link #android.view.View.GONE}.
     */
    void setVisibility(int visibility);

    /**
     * Get the visible state of the view.
     * @return visibility One of {@link #android.view.View.VISIBLE},
     * {@link #android.view.View.INVISIBLE}, or {@link #android.view.View.GONE}.
     */
    int getVisibility();
}

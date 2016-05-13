package com.thebluealliance.androidclient.viewmodels;

public class ListSectionHeaderViewModel extends BaseViewModel {

    private String mTitle;

    public ListSectionHeaderViewModel(String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    @Override public boolean equals(Object o) {
        if(!(o instanceof ListSectionHeaderViewModel)) {
            return false;
        }

        ListSectionHeaderViewModel model = (ListSectionHeaderViewModel) o;

        return mTitle.equals(model.getTitle());
    }

    @Override public int hashCode() {
        return mTitle.hashCode();
    }
}

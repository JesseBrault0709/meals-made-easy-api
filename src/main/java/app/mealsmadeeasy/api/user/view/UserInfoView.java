package app.mealsmadeeasy.api.user.view;

import app.mealsmadeeasy.api.user.User;

public class UserInfoView {

    public static UserInfoView from(User user) {
        final UserInfoView userInfoView = new UserInfoView();
        userInfoView.setId(user.getId());
        userInfoView.setUsername(user.getUsername());
        return userInfoView;
    }

    private long id;
    private String username;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}

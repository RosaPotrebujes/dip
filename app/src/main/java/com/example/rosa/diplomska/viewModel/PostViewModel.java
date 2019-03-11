package com.example.rosa.diplomska.viewModel;
import android.databinding.BaseObservable;
import android.databinding.ObservableBoolean;

import com.example.rosa.diplomska.model.Entity.Post;
import com.example.rosa.diplomska.model.Entity.User;

public class PostViewModel extends BaseObservable{
    public final ObservableBoolean isFavourite = new ObservableBoolean();
    private Post mPost;

    public PostViewModel(Post mPost) {
        this.mPost = mPost;
    }

    //to bo povedal a je fav post od upisanga uporabnika
    public void setIsUserFav(User user) {
        if(user.isUsersFavouritePost(this.mPost))
            this.isFavourite.set(true);
        else
            this.isFavourite.set(false);

        //if(this.mPost.getFavourite().contains(user))
        //    this.isFavourite.set(true);
        //this.isFavourite.set(false);
    }

}

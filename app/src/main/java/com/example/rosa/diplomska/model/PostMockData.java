package com.example.rosa.diplomska.model;

import com.example.rosa.diplomska.model.Entity.Post;
import com.example.rosa.diplomska.model.Entity.User;

import java.sql.Timestamp;
import java.util.ArrayList;

public class PostMockData {
    public static ArrayList<Post> getPosts(){
        ArrayList<Post> allPosts = new ArrayList<>();
        User u1 = new User(1,"Eva Bathory","eva.bathory@eternalife.com");
        User u2 = new User(2,"Janez Novak","janez.novak@tralala.si");
        User u3 = new User(3,"Aljana Kovac","aljana@jana.ja");
        User u4 = new User(4,"Geralt of Rivia","whitewolf@witcher.si"); //brez postov

        //jih dodam v mock bazo

        Post p1 = new Post(u1.getUsername(),"Taking a bath.",new Timestamp(System.currentTimeMillis()));
        Post p2 = new Post(u1.getUsername(),"Looking for a new maid.",new Timestamp(System.currentTimeMillis()));

        u1.addPost(p1);
        u1.addPost(p2);

        Post p3 = new Post(u2.getUsername(),"Plese na Golico hue hue.",new Timestamp(System.currentTimeMillis()));
        Post p4 = new Post(u2.getUsername(),"Mock data lalala.",new Timestamp(System.currentTimeMillis()));
        Post p5 = new Post(u2.getUsername(),"Moar mock data. Yeah baby yeaaaaaah.",new Timestamp(System.currentTimeMillis()));
        Post p6 = new Post(u2.getUsername(),"Mooooaaaar mock dataaaaa. Hue hue hue hue hue hue.",new Timestamp(System.currentTimeMillis()));

        u2.addPost(p3);
        u2.addPost(p4);
        u2.addPost(p5);
        u2.addPost(p6);

        Post p7 = new Post(u3.getUsername(),"Dancing in the Presidium. Hoo yeah!",new Timestamp(System.currentTimeMillis()));
        Post p8 = new Post(u3.getUsername(),"Moooooooooooooooooooooooooooooooock.",new Timestamp(System.currentTimeMillis()));
        Post p9 = new Post(u3.getUsername(),"Dataaaaaa mock mock mock mock mock mock.",new Timestamp(System.currentTimeMillis()));
        Post p10 = new Post(u3.getUsername(),"lalalalalalalala mock data jajajajajaja.",new Timestamp(System.currentTimeMillis()));
        Post p11 = new Post(u3.getUsername(),"aaaaaaaa bbbbbbbb cccccccccc dddddddddddddddd.",new Timestamp(System.currentTimeMillis()));
        Post p12 = new Post(u3.getUsername(),"Noe noe noe noe noe",new Timestamp(System.currentTimeMillis()));
        Post p13 = new Post(u3.getUsername(),"Hue hue hue hue hueh huuuuuuuuuuuueeeeeeee.",new Timestamp(System.currentTimeMillis()));
        Post p14 = new Post(u3.getUsername(),"MOAAAR MOCK DATAAAAAAAA.",new Timestamp(System.currentTimeMillis()));
        Post p15 = new Post(u3.getUsername(),"and some moaaaaar mock dataaaaa. Yeah hahaha muahahaha lalalala",new Timestamp(System.currentTimeMillis()));

        u3.addPost(p7);
        u3.addPost(p8);
        u3.addPost(p9);
        u3.addPost(p10);
        u3.addPost(p11);
        u3.addPost(p12);
        u3.addPost(p13);
        u3.addPost(p14);
        u3.addPost(p15);

        allPosts =  new ArrayList<>();
        allPosts.add(p1);
        allPosts.add(p2);
        allPosts.add(p3);
        allPosts.add(p4);
        allPosts.add(p5);
        allPosts.add(p6);
        allPosts.add(p7);
        allPosts.add(p8);
        allPosts.add(p9);
        allPosts.add(p10);
        allPosts.add(p11);
        allPosts.add(p12);
        allPosts.add(p13);
        allPosts.add(p14);
        allPosts.add(p15);

        return allPosts;
    }
}

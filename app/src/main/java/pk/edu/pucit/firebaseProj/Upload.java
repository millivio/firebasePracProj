package pk.edu.pucit.firebaseProj;

public class Upload {
    private String mName;
    private String mEmail;
    private String mImageUri;

    public Upload()
    {
        //empty constructor;
    }

    public Upload(String name, String email, String imageUrl)
    {
        if(name.trim().equals(""))
        {
            name= "No Name";
        }
        else {
            mName = name;
        }
        mEmail = email;
        mImageUri= imageUrl;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getImageUri() {
        return mImageUri;
    }

    public void setImageUri(String mImageUri) {
        this.mImageUri = mImageUri;
    }
}

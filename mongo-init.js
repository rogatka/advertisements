db.createUser(
    {
      user: "advertisement",
      pwd: "advertisement-password",
      roles: [
        {
          role: "readWrite",
          db: "advertisement"
        }
      ]
    }
);
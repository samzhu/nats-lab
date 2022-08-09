db.auth("user1", "pw123456");

db = db.getSiblingDB("devdoc");

db.createUser({
  user: "user1",
  pwd: "pw123456",
  roles: [
    {
      role: "readWrite",
      db: "devdoc",
    },
  ],
});

db.createCollection('orders');
 
// db.createUser(
//   {
//       user: "user1",
//       pwd: "pw123456",
//       roles: [
//           {
//               role: "readWrite",
//               db: "devdoc"
//           }
//       ]
//   }
// )
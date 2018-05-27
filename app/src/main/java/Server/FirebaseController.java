//package Server;
//
//import android.app.DownloadManager;
//import android.net.Uri;
//import android.os.Environment;
//import android.support.annotation.NonNull;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.storage.FileDownloadTask;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.nio.channels.Channels;
//import java.nio.channels.ReadableByteChannel;
//
//public class FirebaseController {
//    public static void downloadObj() {
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//
//
//        StorageReference storageRef = storage.getReference();
//        StorageReference pathReference = storageRef.child("couchTwoSeats.obj");
//
//
//        try {
//            final File localFile = File.createTempFile("images", "obj");
////
////            pathReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
////
////
////
////                @Override
////                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
////                    // Local temp file has been created
////                    Log.d("Server", "Download success?");
////                    Log.d("Server", localFile.getAbsolutePath());
////
////
////                }
////            }).addOnFailureListener(new OnFailureListener() {
////                @Override
////                public void onFailure(@NonNull Exception exception) {
////                    // Handle any errors
////                    Log.d("Server", "Download failed?");
////                }
////            });
//
//            pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                @Override
//                public void onSuccess(Uri uri) {
//                    Log.d("Server", "uri: " + uri.toString());
//                    //Handle whatever you're going to do with the URL here
//
//
//                    try {
//
//                        URL url = new URL(uri.toString());//Create Download URl
//                        HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
//                        c.setRequestMethod("GET");//Set Request Method to "GET" since we are grtting data
//                        c.connect();//connect the URL Connection
//                        File apkStorage = new File(
//                                Environment.getExternalStorageDirectory() + "/downloasTest");
//                        if (!apkStorage.exists()) {
//                            apkStorage.mkdir();
//                            Log.d("Server", "Directory Created.");
//                        }
//                        File outputFile = new File("couch.obj");
//                        if (!outputFile.exists()) {
//                            outputFile.createNewFile();
//                            Log.e("Server", "File Created");
//                        }
//
//                    } catch (MalformedURLException e) {
//                        e.printStackTrace();
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            });
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//}

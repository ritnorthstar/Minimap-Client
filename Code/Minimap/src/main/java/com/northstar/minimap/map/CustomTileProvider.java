//John Paul Mardelli
//Last updated November 19th, 2013

package com.northstar.minimap.map;


import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;

import android.graphics.*;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class CustomTileProvider implements TileProvider {

    private byte mapPicture[];
    private String imageFile;
    private int height;
    private int width;

    /**
     * Constructor used for testing with static pictures
     * @param imageFile
     * @param height
     * @param width
     */
    public CustomTileProvider(String imageFile, int height, int width){
        this.imageFile = imageFile;
        this.height = height;
        this.width = width;
    }

    public CustomTileProvider(byte[] data, int height, int width){
        mapPicture = data;
        this.height = height;
        this.width = width;
    }

    public Tile getTile(int x, int y, int zoom){
        return new Tile(width, height, mapPicture);
    }

    /**
     * @param image
     * @return
     * @throws IOException
     */
    public byte[] getImage (String imageName) throws IOException {
    	File sdCard = Environment.getExternalStorageDirectory();
    	File directory = new File (sdCard.getAbsolutePath() + "/Pictures");
    	File file = new File(directory, imageName); //or any other format supported
    	FileInputStream streamIn = new FileInputStream(file);
    	Bitmap bmp = BitmapFactory.decodeStream(streamIn); //This gets the image
    	streamIn.close();
    	ByteArrayOutputStream stream = new ByteArrayOutputStream();
    	bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

}

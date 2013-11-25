//John Paul Mardelli
//Last updated November 19th, 2013

package com.northstar.minimap.map;


import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;

import java.awt.image.*;
import java.io.File;
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
     *
     * Code gotten from http://stackoverflow.com/questions/17202241/how-to-make-custom-tiles-in-android-google-maps
     *
     * @param image
     * @return
     * @throws IOException
     */
    public byte[] getImage (String image) throws IOException {
        File yourImg = new File(image);
        BufferedImage bufferedImage = ImageIO.read(yourImg);
        WritableRaster wRaster = bufferedImage.getRaster();
        DataBufferByte data   = (DataBufferByte) wRaster.getDataBuffer();

        mapPicture = data.getData();
    }

}

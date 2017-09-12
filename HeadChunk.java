/**
  * MIDIファイルのヘッダチャンクを表すクラス
  * @author Yutaro Inoue
  */

import java.util.Arrays;
import java.nio.ByteBuffer;

class HeadChunk {
    private byte[] chunkType;
    private byte[] headChunkSize;
    private byte[] format;
    private byte[] trackSize;
    private byte[] resolution;
    
    HeadChunk(byte[] headChunkData) {
        this.chunkType = Arrays.copyOfRange(headChunkData, 0, 4);
        this.headChunkSize = Arrays.copyOfRange(headChunkData, 4, 8);
        this.format = Arrays.copyOfRange(headChunkData, 8, 10);
        this.trackSize = Arrays.copyOfRange(headChunkData, 10, 12);
        this.resolution = Arrays.copyOfRange(headChunkData, 12, 14);
    }
    
    public int getTrackSize() {
        byte[] b = new byte[4];
        b[3] = trackSize[1];
        b[2] = trackSize[0];
        b[1] = 0;
        b[0] = 0;
        int size = ByteBuffer.wrap(b).getInt();
        return size;
    }
}

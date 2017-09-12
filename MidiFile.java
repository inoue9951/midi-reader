/** MIDIファイルを表すクラス
  * @author Yutaro Inoue
  */

import java.util.Arrays;
import java.io.*;
import java.nio.ByteBuffer;

public class MidiFile {
    HeadChunk headChunk = null;
    TrackChunk[] trackChunk = null;

    MidiFile(String filePath) {
        //Midiファイルを読み込む
        byte[] byteMidiData = null;
        String strMidiData = null;
        try {
            byteMidiData = readMidiToByte(filePath);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        //読み込んだデータをヘッダチャンクとデータチャンクに分割する
        byte[] headChunkData = Arrays.copyOfRange(byteMidiData, 0, 14);
        byte[] trackChunkData = Arrays.copyOfRange(byteMidiData, 14, byteMidiData.length);
        headChunk = new HeadChunk(headChunkData);
        setTrackChunk(trackChunkData);
    }

    /**
      * MIDIファイルを読み込む.
      * 引数としてMIDIファイルのパスを渡すとbyte[]が返される.
      * @param String filePath midiファイルのパス
      * @return byte[] midiFile 読み込んだmidiファイルのバイト配列
      */
    private byte[] readMidiToByte(String filePath) throws Exception {
        byte[] b = new byte[1];
        FileInputStream fis = new FileInputStream(filePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while (fis.read(b) > 0) {
            baos.write(b);
        }
        fis.close();
        baos.close();
        byte[] midiFile = baos.toByteArray();
        return midiFile;
    }

    private void setTrackChunk(byte[] data) {
        //Track数を求める
        int trackSize = getTrackSize();
        trackChunk = new TrackChunk[trackSize];
        //Track数だけループする
        for (int i = 0; i < trackSize; i++) {
            //チャンクタイプが正しいか確かめる
            if (data[0] == (byte)0x4d && data[1] == (byte)0x54 &&
                data[2] == (byte)0x72 && data[3] == (byte)0x6b) {
                //データ本体のサイズを求める
                int trackDataSize = ByteBuffer.wrap(Arrays.copyOfRange(data, 4, 8)).getInt();
                //データ本体を切り分ける
                byte[] trackChunkData = Arrays.copyOfRange(data, 0 ,trackDataSize + 8);
                trackChunk[i] = new TrackChunk(trackChunkData, trackDataSize);
                data = Arrays.copyOfRange(data, trackDataSize + 8, data.length);
                //printByte(trackChunkData);
            } else {
                System.out.println("ファイルがおかしいです");
            }
        }
    }

    public void printByte(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02X", b));
        }
        String str = sb.toString();
        System.out.println(str);
    }


    public int getTrackSize() {
        return headChunk.getTrackSize();
    }


}

package Examples.QRtoHAL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import HAL.GridsAndAgents.AgentGrid2D;
import HAL.GridsAndAgents.AgentSQ2Dunstackable;
import HAL.Gui.GifMaker;
import HAL.Gui.UIGrid;
import HAL.Rand;
import HAL.Util;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;


// for this example to work correctly, you must go to Project Structure->Modules-> Dependencies
// ... and add the 2 jar files in Examples/QRtoHAL/ZXing (used for QR creation)

//QR pixel (or a "QR agent")
class QRAgent extends AgentSQ2Dunstackable<MyQr> {
    public void Move() {
        if (G.rn.Double() < 0.05) {
            int n = G.MapEmptyHood(G.hood, Isq());
            if (n > 0) {
                G.NewAgentSQ(G.hood[G.rn.Int(n)]);
                this.Dispose();
            }
        }
    }
}

public class MyQr extends AgentGrid2D<QRAgent> {
    Rand rn = new Rand();
    BitMatrix matrix;
    int[] hood = Util.MooreHood(false);
    public void Step() {
        for (QRAgent qrAgent: this) {
            qrAgent.Move();
        }
    }

    public MyQr(String data, int sf) throws WriterException, IOException {
        super(33,33,QRAgent.class);

        int size = 33; // minimum
        // build QR:
        String charset = "UTF-8";
        Map<EncodeHintType, ErrorCorrectionLevel> hashMap = new HashMap<EncodeHintType, ErrorCorrectionLevel>();
        hashMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        BitMatrix matrix = new MultiFormatWriter().encode(new String(data.getBytes(charset), charset), BarcodeFormat.QR_CODE, size, size);

        int matSize = matrix.getWidth();
        for(int y = 0; y < matSize; ++y) {
            for(int x = 0; x < matSize; ++x) {
                if (matrix.get(x,y)) {
                    NewAgentSQ(x,y);
                }
            }
        }
    }

    public UIGrid Draw(UIGrid myGrid) {
        for (int i = 0; i < myGrid.length; i++) {
            if (GetAgent(i) != null ){
                myGrid.SetPix(i, Util.BLACK);
            } else {
                myGrid.SetPix(i, Util.WHITE);
            }
        }
        return myGrid;
    }

    // Driver code
    public static void main(String[] args)
            throws WriterException, IOException,
            NotFoundException
    {

        // The data that the QR code will contain
        String data = "http://halloworld.org";

        // start window
        int scale_factor = 15;
        MyQr QR = new MyQr(data,scale_factor);

        ArrayList<UIGrid> grids = new ArrayList<>();

        for (int i = 0; i < 40; i++) {
            UIGrid myGrid = new UIGrid(33,33,scale_factor);
            grids.add(QR.Draw(myGrid));
            QR.Step();
        }

        // add them to gif in reverse order, so QR is at the end
        GifMaker gm = new GifMaker("Examples/QRtoHAL/QR.gif",100,false);
        for (int i = 0; i < grids.size(); i++) {
            gm.AddFrame(grids.get(grids.size()-i-1));
        }

        // some more QR frames at the end
        for (int i = 0; i < 40; i++) {
            gm.AddFrame(grids.get(0));
        }
    }
}
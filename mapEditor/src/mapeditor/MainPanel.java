package mapeditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.nio.ByteBuffer;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

/*
 * Created on 2005/12/25
 *
 */

/**
 * @author mori
 *
 */
public class MainPanel extends JPanel
        implements
            MouseListener,
            MouseMotionListener {


    // チップセットのサイズ（単位：ピクセル）
    public static final int CHIP_SIZE = 32;

    // パネルのサイズ（単位：ピクセル）
    private static final int WIDTH = 640;
    private static final int HEIGHT = 640;
    
    // レイヤー
    private LayerPanel[] layers;
    private int current_layer;

    // マップチップパレット
    private PaletteDialog paletteDialog;
    private AutoTilePaletteDialog autoTileDialog;

    // JPanel
    private JPanel checkbox_panel;
    private JPanel layer_panel;
    private final int CHECKBOX_HEIGHT_OFFSET = 30;
    
    public MainPanel(PaletteDialog paletteDialog, AutoTilePaletteDialog autoTileDialog) {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        addMouseListener(this);
        addMouseMotionListener(this);

        // パレットダイアログ
        this.paletteDialog = paletteDialog;
        this.autoTileDialog = autoTileDialog;
       
        // レイヤーを初期化
        initLayer(16, 16, 3);
    }
    
    public void initLayer(int r, int c, int lnum) {
    	
    	// init checkbox panel and layer panel
        this.setLayout(null);
        checkbox_panel = new JPanel();
        checkbox_panel.setBounds(0, 0, lnum * 100, CHECKBOX_HEIGHT_OFFSET);
        layer_panel = new JPanel();
        layer_panel.setBounds(0, CHECKBOX_HEIGHT_OFFSET, c * CHIP_SIZE, CHECKBOX_HEIGHT_OFFSET + r * CHIP_SIZE);
        layer_panel.setBorder(new LineBorder(Color.black, 1));
        this.add(checkbox_panel);
        this.add(layer_panel);
        
    	// init checkbox
        checkbox_panel.setLayout(new BoxLayout(checkbox_panel, BoxLayout.X_AXIS));
    	for (int i = 0; i < lnum; i++) {
    		JCheckBox checkbox = new JCheckBox("layer"+(i+1));
            checkbox_panel.add(checkbox);
    	}
    	// init layer
    	current_layer = 1;
    	layers = new LayerPanel[lnum];
    	JCheckBox checkbox = new JCheckBox("layerx");
    	layer_panel.add(checkbox);
        layer_panel.setLayout(null);
    	for (int i = 0; i < lnum; i++) {
    		LayerPanel layer = new LayerPanel(r, c, paletteDialog, autoTileDialog);
    		layer.setBounds(0, 0, c * CHIP_SIZE, r * CHIP_SIZE);
    		layer_panel.add(layer);
    		layers[i] = layer;
    	}
    }


    /**
     * マップをファイルから読み込む
     * @param mapFile
     */
    public void loadMap(File mapFile) {
//        try {
//            FileInputStream in = new FileInputStream(mapFile);
//            // 行数・列数を読み込む
//            row = in.read();
//            col = in.read();
//            // マップを読み込む
//            map = new int[row][col];
//            for (int i = 0; i < row; i++) {
//                for (int j = 0; j < col; j++) {
//                	byte[] b = new byte[4];
//                	in.read(b, 0, 4);
//                    map[i][j] = fromBytes(b);
//                }
//            }
//            in.close();
//
//            // パネルの大きさをマップの大きさと同じにする
//            setPreferredSize(new Dimension(col * CHIP_SIZE, row * CHIP_SIZE));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * byte配列変換
     *
     * @param value
     *            変換対象の値
     * @return byte配列に変換された値
     */
    public static byte[] fromInt(int value) {
        int arraySize = Integer.SIZE / Byte.SIZE;
        ByteBuffer buffer = ByteBuffer.allocate(arraySize);
        return buffer.putInt(value).array();
    }

    static int fromBytes(byte[] ba) {
        return ByteBuffer.wrap(ba).getInt();
    }

    /**
     * マップをファイルへ書き込む
     * @param mapFile マップファイル
     */
    public void saveMap(File mapFile) {
//        // マップはバイナリファイルにする
//        // マップの1マスを1バイトとする
//        try {
//            FileOutputStream out = new FileOutputStream(mapFile);
//            // 行数・列数を書き込む
//            out.write(row);
//            out.write(col);
//            // マップを書き込む
//            for (int i = 0; i < row; i++) {
//                for (int j = 0; j < col; j++) {
//                    out.write(fromInt(map[i][j]));
//                }
//            }
//            out.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // // map[][]に保存されているマップチップ番号をもとに画像を描画する
        // for (int i = 0; i < row; i++) {
        //     for (int j = 0; j < col; j++) {
        //     	int chip_id = map[i][j];
        //     	if (autoTileDialog.isAutoTile(chip_id)) {
        //             g.drawImage(autoTileDialog.getMapChipImage(i, j), j * CHIP_SIZE, i * CHIP_SIZE, null);
        //     	}else {
        //     		g.drawImage(paletteDialog.getMapChipImage(chip_id), j * CHIP_SIZE, i * CHIP_SIZE, null);
        //     	}
        //     }
        // }
    }

    /**
     * マウスでマップ上をクリックしたとき
     */
    public void mouseClicked(MouseEvent e) {
        // マウスポインタの座標から座標（マス）を求める
        int x = e.getX() / CHIP_SIZE;
        int y = (e.getY() - CHECKBOX_HEIGHT_OFFSET) / CHIP_SIZE;
        
        System.out.println("Current Layer:" + current_layer);
        layers[current_layer].setIdOnMap(y, x);

        repaint();
        layers[current_layer].repaint();
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    /**
     * マップ上でマウスをドラッグしたとき
     */
    public void mouseDragged(MouseEvent e) {
        // マウスポインタの座標から座標（マス）を求める
        int x = e.getX() / CHIP_SIZE;
        int y = (e.getY() - CHECKBOX_HEIGHT_OFFSET) / CHIP_SIZE;

        System.out.println("Current Layer:" + current_layer);
        layers[current_layer].setIdOnMap(y, x);

        repaint();
        layers[current_layer].repaint();
    }

    public void mouseMoved(MouseEvent e) {
    }
}
package mapeditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
            MouseMotionListener,
            ChangeListener {


    // チップセットのサイズ（単位：ピクセル）
    public static final int CHIP_SIZE = 32;

    // メインパネルのサイズ（単位：ピクセル）
    private static final int WIDTH = 640;
    private static final int HEIGHT = 640;
    
    // レイヤー
    private LayerPanel[] layers;
    private int current_layer;
    private static final int LAYER_NUM = 3;

    // マップサイズ
    private int row;
    private int col;
    
    // マップチップパレット
    private PaletteDialog paletteDialog;
    private AutoTilePaletteDialog autoTileDialog;

    // JPanel
    private JPanel checkbox_panel;
    private JPanel radiobutton_panel;
    private JPanel layer_panel;
    private final int PANEL_HEIGHT_OFFSET = 20;
    
    public MainPanel(PaletteDialog paletteDialog, AutoTilePaletteDialog autoTileDialog) {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        addMouseListener(this);
        addMouseMotionListener(this);

        // パレットダイアログ
        this.paletteDialog = paletteDialog;
        this.autoTileDialog = autoTileDialog;
       
        // レイヤー, パネルを初期化
        row = 16;
        col = 16;
        int mapchip = 2000;
        initLayer(row, col, mapchip);
    }
    
    public void initLayer(int r, int c, int init_mapchip) {
    	
    	// init panels
        this.setLayout(null);
        JLabel note_label = new JLabel("layer  order:   <--奥      手前-->");
        note_label.setBounds(0, 0, 250, PANEL_HEIGHT_OFFSET);
        this.add(note_label);
        checkbox_panel = new JPanel();
        checkbox_panel.setBounds(0, PANEL_HEIGHT_OFFSET, LAYER_NUM * 100, PANEL_HEIGHT_OFFSET);
        this.add(checkbox_panel);
        radiobutton_panel = new JPanel();
        radiobutton_panel.setBounds(0, 2 * PANEL_HEIGHT_OFFSET, LAYER_NUM * 100, PANEL_HEIGHT_OFFSET);
        this.add(radiobutton_panel);
        layer_panel = new JPanel();
        layer_panel.setBounds(0, 3 * PANEL_HEIGHT_OFFSET, c * CHIP_SIZE, r * CHIP_SIZE);
        layer_panel.setBorder(new LineBorder(Color.black, 1));
        this.add(layer_panel);
        
    	// init checkbox
        checkbox_panel.setLayout(new BoxLayout(checkbox_panel, BoxLayout.X_AXIS));
        checkbox_panel.add(new JLabel("show  layer: "));
    	for (int i = 0; i < LAYER_NUM; i++) {
    		JCheckBox checkbox = new JCheckBox("layer"+(i+1), true);
    		checkbox.addChangeListener(this);
            checkbox_panel.add(checkbox);
    	}
    	// init radio button
        radiobutton_panel.setLayout(new BoxLayout(radiobutton_panel, BoxLayout.X_AXIS));
        ButtonGroup bg = new ButtonGroup();
        radiobutton_panel.add(new JLabel("select layer: "));
    	for (int i = 0; i < LAYER_NUM; i++) {
    		JRadioButton radiobutton = new JRadioButton("layer"+(i+1));
    		if (i == 0) {
    			radiobutton.setSelected(true);;
    		}
    		radiobutton.addChangeListener(this);
    		bg.add(radiobutton);
            radiobutton_panel.add(radiobutton);
    	}
    	// init layer
    	current_layer = 0;
    	layers = new LayerPanel[LAYER_NUM];
    	JCheckBox checkbox = new JCheckBox("layerx");
    	layer_panel.add(checkbox);
        layer_panel.setLayout(null);
    	for (int i = LAYER_NUM - 1; i >= 0; i--) {
    		LayerPanel layer = new LayerPanel(i, r, c, init_mapchip, paletteDialog, autoTileDialog);
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
        try {
            FileInputStream in = new FileInputStream(mapFile);
            // 行数・列数を読み込む
            row = in.read();
            col = in.read();
            // マップを読み込む レイヤー1から順に
            for (int l = 0; l < LAYER_NUM; l++){
            	layers[l].map = new int[row][col];
                for (int i = 0; i < row; i++) {
                    for (int j = 0; j < col; j++) {
                    	byte[] b = new byte[4];
                    	in.read(b, 0, 4);
                        layers[l].map[i][j] = fromBytes(b);
                    }
                }
                layers[l].updateAllMapChipImage();
            }
            in.close();

            // パネルの大きさをマップの大きさと同じにする
            setPreferredSize(new Dimension(col * CHIP_SIZE, row * CHIP_SIZE));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        // マップはバイナリファイルにする
        // マップの1マスを1バイトとする
        try {
            FileOutputStream out = new FileOutputStream(mapFile);
            // 行数・列数を書き込む
            out.write(row);
            out.write(col);
            // マップを書き込む レイヤーを1から順に
            for (int l = 0; l < LAYER_NUM; l++){
                for (int i = 0; i < row; i++) {
                    for (int j = 0; j < col; j++) {
                        out.write(fromInt(layers[l].map[i][j]));
                    }
                }
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * マウスでマップ上をクリックしたとき
     */
    public void mouseClicked(MouseEvent e) {
        // マウスポインタの座標から座標（マス）を求める
        int x = e.getX() / CHIP_SIZE;
        int y = (e.getY() - 3 * PANEL_HEIGHT_OFFSET) / CHIP_SIZE;
        
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
        int y = (e.getY() - 3 * PANEL_HEIGHT_OFFSET) / CHIP_SIZE;

        layers[current_layer].setIdOnMap(y, x);

        repaint();
        layers[current_layer].repaint();
    }

    public void mouseMoved(MouseEvent e) {
    }

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource().getClass().equals(JRadioButton.class)){
			JRadioButton target = (JRadioButton) e.getSource();
			if (target.isSelected()) {
				current_layer = Character.getNumericValue(target.getText().charAt(target.getText().length() - 1)) - 1;
			}
		}
		else if (e.getSource().getClass().equals(JCheckBox.class)){
			JCheckBox target = (JCheckBox) e.getSource();
			int target_num = Character.getNumericValue(target.getText().charAt(target.getText().length() - 1)) - 1;
			layers[target_num].setVisible(target.isSelected());
		}
	}
}
package Build2048;

//Tao Jpanel.
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class MakingGame2048 extends JPanel {

	//~Add default serial versionID(Game)	
	private static final long serialVersionUID = 1L;
	// mau Background 
	private static final Color BG_COLOR = new Color(0x1b163d);
	//dat font.
	private static final String FONT_NAME = "Bebas Neue Regular";
	
	private static final int TILE_SIZE = 64;		//size cho tile.
	private static final int TILES_MARGIN = 15;		//kcach giua cac tile

	private Tile[] myTiles;
	boolean Thang  = false;		
	boolean Thua = false;
	int DiemSo = 0;		//khai bao bien DIEM.

  public MakingGame2048() {
    setFocusable(true);
    
    //#
    addKeyListener(new KeyAdapter() {
    	
      @Override
      public void keyPressed(KeyEvent e) {
    	  //~Su kien khi nhan input phim
        if (e.getKeyCode() == KeyEvent.VK_N) {
        	
        	//Bam phim ESC de newgame.
          resetGame();
        }
        
        if (!canMove()) {
          Thua = true;		//neu khong di chuyen duoc nua => thua.
        }

        if (!Thang && !Thua) {
        	//neu khong thang, khong thua.
          switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
              trai();
              break;
              
            case KeyEvent.VK_RIGHT:
              phai();
              break;
              
            case KeyEvent.VK_DOWN:
              duoi();
              break;
              
            case KeyEvent.VK_UP:
              tren();
              break;
          }
        }

        if (!Thang && !canMove()) {
          Thua = true;		//neu khong thang, khong di duoc nua => thua.
        }

        repaint();
      }
    });
    resetGame();		
  }
  
//#
//Ham newgame.
  public void resetGame() {
    DiemSo = 0;
    Thang = false;
    Thua = false;
    myTiles = new Tile[4 * 4];
    for (int i = 0; i < myTiles.length; i++) {
      myTiles[i] = new Tile();
    }
    themTile();
    themTile();		//2 addTile => random 2 tile bat ki.
  }

  //#
  public void trai() {
    boolean canThemTile = false;		//ktra.
    for (int i = 0; i < 4; i++) {
      Tile[] line = getLine(i);		//lay dong.
      Tile[] merged = gopO(diChuyen(line));	//gop tile.
      setLine(i, merged);
      if (!canThemTile && !compare(line, merged)) {
    	  //ktra neu ko add tile va sosannh 
    	  canThemTile = true;
      }
    }
//#
    if (canThemTile) {
    	themTile();
    }
  }
//#
  public void phai() {
    myTiles = rotate(180);
    trai();
    myTiles = rotate(180);
  }
//#
  public void tren() {
    myTiles = rotate(270);
    trai();
    myTiles = rotate(90);
  }
//#
  public void duoi() {
    myTiles = rotate(90);
    trai();
    myTiles = rotate(270);
  }

  private Tile tileAt(int x, int y) {
    return myTiles[x + y * 4];
  }

  //Ham them tile.
  private void themTile() {
    List<Tile> list = choTrong();
    if (!choTrong().isEmpty()) {
      int index = (int) (Math.random() * list.size()) % list.size();
      Tile emptyTime = list.get(index);
      emptyTime.value = Math.random() < 0.9 ? 2 : 4;
    }
  }
  
  //#
  //Ham ktra neu con cho trong.
  private List<Tile> choTrong() {
    final List<Tile> list = new ArrayList<Tile>(16);
    for (Tile t : myTiles) {
      if (t.isEmpty()) {
        list.add(t);
      }
    }
    return list;
  }

  //Ham ktra neu k con cho trong.
  private boolean isFull() {
    return choTrong().size() == 0;
  }

  //Ham ktra di duoc k.
  boolean canMove() {
    if (!isFull()) {
      return true;
    }
    for (int x = 0; x < 4; x++) {
      for (int y = 0; y < 4; y++) {
        Tile t = tileAt(x, y);
        if ((x < 3 && t.value == tileAt(x + 1, y).value)
          || ((y < 3) && t.value == tileAt(x, y + 1).value)) {
          return true;
        }
      }
    }
    return false;
  }

  //Ham So Sanh giua 2 tile 
  private boolean compare(Tile[] line1, Tile[] line2) {
    if (line1 == line2) {
      return true;
    } else if (line1.length != line2.length) {
      return false;
    }

    for (int i = 0; i < line1.length; i++) {
      if (line1[i].value != line2[i].value) {
        return false;
      }
    }
    return true;
  }

  private Tile[] rotate(int angle) {
    Tile[] newTiles = new Tile[4 * 4];
    int offsetX = 3, offsetY = 3;
    if (angle == 90) {
      offsetY = 0;
    } else if (angle == 270) {
      offsetX = 0;
    }

    double rad = Math.toRadians(angle);
    int cos = (int) Math.cos(rad);
    int sin = (int) Math.sin(rad);
    for (int x = 0; x < 4; x++) {
      for (int y = 0; y < 4; y++) {
        int newX = (x * cos) - (y * sin) + offsetX;
        int newY = (x * sin) + (y * cos) + offsetY;
        newTiles[(newX) + (newY) * 4] = tileAt(x, y);
      }
    }
    return newTiles;
  }

  private Tile[] diChuyen(Tile[] oldLine) {
    LinkedList<Tile> l = new LinkedList<Tile>();
    for (int i = 0; i < 4; i++) {
      if (!oldLine[i].isEmpty())
        l.addLast(oldLine[i]);
    }
    if (l.size() == 0) {
      return oldLine;
    } else {
      Tile[] newLine = new Tile[4];
      ensureSize(l, 4);
      for (int i = 0; i < 4; i++) {
        newLine[i] = l.removeFirst();
      }
      return newLine;
    }
  }

  private Tile[] gopO(Tile[] oldLine) {
    LinkedList<Tile> list = new LinkedList<Tile>();
    for (int i = 0; i < 4 && !oldLine[i].isEmpty(); i++) {
      int num = oldLine[i].value;
      if (i < 3 && oldLine[i].value == oldLine[i + 1].value) {
        num *= 2;
        DiemSo += num;
        int Target = 64;		//So =2048 =>Thang.
        if (num == Target) {
          Thang = true;
        }
        i++;
      }
      list.add(new Tile(num));
    }
    if (list.size() == 0) {
      return oldLine;
    } else {
      ensureSize(list, 4);
      return list.toArray(new Tile[4]);
    }
  }

  private static void ensureSize(java.util.List<Tile> l, int s) {
    while (l.size() != s) {
      l.add(new Tile());
    }
  }

  private Tile[] getLine(int index) {
    Tile[] result = new Tile[4];
    for (int i = 0; i < 4; i++) {
      result[i] = tileAt(i, index);
    }
    return result;
  }

  private void setLine(int index, Tile[] re) {
    System.arraycopy(re, 0, myTiles, index * 4, 4);
  }

  //#
  @Override
  public void paint(Graphics g) {
    super.paint(g);
    g.setColor(BG_COLOR);
    g.fillRect(0, 0, this.getSize().width, this.getSize().height);
    for (int y = 0; y < 4; y++) {
      for (int x = 0; x < 4; x++) {
        toMau(g, myTiles[x + y * 4], x, y);
      }
    }
  }
//#
  private void toMau(Graphics g2, Tile tile, int x, int y) {
	  
	 //Sua dung library de ve hinh 2D.
    Graphics2D g = ((Graphics2D) g2);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
    
    int value = tile.value;
    
    int toaDoX = offsetCoors(x);
    int toaDoY = offsetCoors(y);
    
    g.setColor(tile.getBackground());		//set mau cho tile 2-4-8-16 dua vao phuong thuc o duoi.
    
   g.fillRoundRect(toaDoX, toaDoY, TILE_SIZE, TILE_SIZE, 14, 14);		//khoanh vung o vuong lai.
    
    g.setColor(tile.getForeground());		//set mau chu.
    final int size = value < 100 ? 36 : value < 1000 ? 32 : 24;		//set size chu neu so < 100 thi size =36, size <1000 thi size = 32, >1000 size=24.
    
    final Font font = new Font(FONT_NAME, Font.BOLD, size);
    g.setFont(font);

    String s = String.valueOf(value);
    final FontMetrics fm = getFontMetrics(font);

    final int w = fm.stringWidth(s);
    final int h = -(int) fm.getLineMetrics(s, g).getBaselineOffsets()[2];

    if (value != 0)
      g.drawString(s, toaDoX + (TILE_SIZE - w) / 2, toaDoY + TILE_SIZE - (TILE_SIZE - h) / 2 - 2);

    if (Thang || Thua) {
      g.setColor(new Color(255, 255, 255, 100));			//set mau background
      g.fillRect(0, 0, getWidth(), getHeight());
      
      g.setColor(new Color(78, 139, 202));		//set chu khi thang.
      g.setFont(new Font(FONT_NAME, Font.BOLD, 48));
      
      if (Thang) {
        g.drawString("Chien Thang!", 120, getHeight() - 200);
      }
      
      if (Thua) {
        g.drawString("Thua Roi!", 120, getHeight() - 200);        
      }
      
      if (Thang || Thua) {
        g.setFont(new Font(FONT_NAME, Font.PLAIN, 30));
        g.setColor(new Color(236, 28, 34, 128));
        g.drawString("Bam phim N de new game.", 80, getHeight() - 40);
      }
    }
    g.setFont(new Font(FONT_NAME, Font.PLAIN, 35));
    g.drawString("Game 2048", 330, 50);
    
    g.setFont(new Font(FONT_NAME, Font.PLAIN, 30));
    g.drawString("Diem: " + DiemSo, 330, 200);		//ngang, dai.

  }

  private static int offsetCoors(int arg) {
    return arg * (TILES_MARGIN + TILE_SIZE) + TILES_MARGIN;
  }

  static class Tile {
    int value;

    public Tile() {
      this(0);
    }

    public Tile(int num) {
      value = num;
    }

    public boolean isEmpty() {
      return value == 0;
    }

    public Color getForeground() {
    	//Ham set mau chu cho tile.    	
      return  new Color(0xffffff);
    }

    public Color getBackground() {
    	//set mau cho tung tile 2,4,8,16,32.
      switch (value) {
        case 2:    return new Color(0x8180d7);
        case 4:    return new Color(0xa25ad6);
        case 8:    return new Color(0x2669dd);
        case 16:   return new Color(0x21b9d5);
        case 32:   return new Color(0x00ca9b);
        case 64:   return new Color(0x43cf17);
        case 128:  return new Color(0xf7c001);
        case 256:  return new Color(0xf58114);
        case 512:  return new Color(0xff1491);
        case 1024: return new Color(0xc3c3c3);
        case 2048: return new Color(0xff143b);
      }
      return new Color(0x302c4f);			//tile trong ko co so. (empty)
    }
  }

  public static void main(String[] args) {
    JFrame game = new JFrame();
    game.setTitle("2048");
    game.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    game.setSize(550, 370);	//ngang, dai.
    game.setResizable(false);	//resize khung game=false

    game.add(new MakingGame2048());

    game.setLocationRelativeTo(null);
    game.setVisible(true);
  }
}


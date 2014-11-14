package com.infiniboard.greenteam.infiniboard;

import android.app.Dialog;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


/**
 * Created by Nick on 10/6/2014.
 */

public class Board extends View {
    //set local variables

    private String name;
    private String description;
    private String timeCreated;
    private Path drawPath;
    private Paint drawPaint, canvasPaint;
    private Bitmap canvasBitmap;
    private Canvas drawCanvas;
    public Marker[] tray = new Marker[5];
    private Marker currentMarker;
    float lastEventX;
    float lastEventY;
    boolean inSelector;
    float originX;
    float originY;
    Selector selector = new Selector(this);
    boolean inMoveMode = false;
    boolean inPasteMode = false;
    private final int LIMIT = 6; //Number of SubBoards
    private ArrayList<Bitmap> subBitmaps = new ArrayList<Bitmap>(0);
    public ArrayList<Anchor> anchors = new ArrayList<Anchor>(0);


    public Board(Context context, AttributeSet attrs) {
        super(context, attrs);
        timeCreated = getTimeCreated();
        //initializes the four markers for the tray
        tray[0] = new Marker(0xFF000000, 0);
        tray[1] = new Marker(0xFF009150, 1);
        tray[2] = new Marker(0xFF0070BB, 2);
        tray[3] = new Marker(0xFFDA2C43, 3);
        tray[4] = new Marker(0xFFffffff, 4, 45);
        currentMarker = tray[0];
        setupDrawing();
    }

    //get drawing area setup for interaction
    private void setupDrawing() {
        drawPath = new Path();
        drawPaint = new Paint();

        drawPaint.setStrokeWidth(currentMarker.getStrokeWidth());
        drawPaint.setStrokeWidth(currentMarker.getStrokeWidth());

        drawPaint.setAntiAlias(true);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        //more research needed here
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    //this is called for when the view needs to render its content
    protected void onDraw(Canvas canvas) {
        //this draws the bitmap to the canvas so it stays after the user lifts their finger
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        //this draws the path as the user is touching the screen
        canvas.drawPath(drawPath, drawPaint);
    }

    //This sets up the canvas by first creating the bitmap where the pixels will be saved
    //and from there creating the canvas that will handle the draw calls
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //view given size
        if(subBitmaps.size()==0) {
            super.onSizeChanged(w, h, oldw, oldh);
            System.out.println(w + " " + h);
            canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            subBitmaps.add(canvasBitmap);
            drawCanvas = new Canvas(canvasBitmap);
        }
    }

    //These are the instructions to draw, when ever the user is touching
    //the screen this method is called
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //get the coordinates of the touch event.
        float eventX = event.getX();
        float eventY = event.getY();
        if (inSelector) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    drawPaint.setColor(0xFF000000);
                    drawPaint.setStrokeWidth(5);
                    drawPaint.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));
                    drawPath.moveTo(eventX, eventY);
                    originX = eventX;
                    originY = eventY;
                    break;
                case MotionEvent.ACTION_MOVE:
                    drawPath.reset();
                    if ((originX < eventX) && (originY < eventY)) {
                        drawPath.addRect(originX, originY, eventX, eventY, Path.Direction.CW);
                    }
                    break;
                case MotionEvent.ACTION_UP:

                    drawPath.reset();
                    if ((originX < eventX) && (originY < eventY)) {
                        selector.setSelection(Bitmap.createBitmap(canvasBitmap, (int) originX, (int) originY, (int) (eventX - originX), (int) (eventY - originY)));
                        selector.setCurrentX((int) originX);
                        selector.setCurrentY((int) originY);
                    }
                    drawPaint.setPathEffect(null);
                    break;
                default:
                    return false;
            }
        } else if (inMoveMode) {
            raiseFlag(eventX, eventY);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //place selection down
                    addSelectionToCanvas(((int) eventX - (selector.getWidth() / 2)), ((int) eventY - (selector.getHeight() / 2)));
                    break;
                case MotionEvent.ACTION_UP:
                    //end move mode
                    inMoveMode = false;
            }
        } else if (inPasteMode) {
            raiseFlag(eventX, eventY);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //place selection down
                    selector.pasteSelection(((int) eventX - (selector.clipboard.getWidth() / 2)), ((int) eventY - (selector.clipboard.getHeight() / 2)));
                    break;
                case MotionEvent.ACTION_UP:
                    //end move mode
                    inPasteMode = false;
            }
        } else {
            if (!(event.getPointerCount() > 1)) {
                if (currentMarker.getID() != 4)
                    raiseFlag(eventX, eventY);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //set a new starting point
                        drawPaint.setColor(currentMarker.getColor());
                        drawPaint.setStrokeWidth(currentMarker.getStrokeWidth());
                        drawCanvas.drawPoint(eventX, eventY, drawPaint);
                        drawPath.moveTo(eventX, eventY);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //Connect the points
                        //This should be slightly smother than the line to became it uses quad equation
                        drawPath.quadTo(lastEventX, lastEventY, eventX, eventY);
                        break;
                    case MotionEvent.ACTION_UP:
                        drawCanvas.drawPath(drawPath, drawPaint);
                        if (name == null) {
                            firstTimeSequence();
                        }
                        drawPath.reset();
                        break;
                    default:
                        return false;
                }
            }
        }
        if (allFlagsTrue())
            expandBoard();

        lastEventX = eventX;
        lastEventY = eventY;
        //makes view repaint and call onDraw
        invalidate();
        return true;
    }


    public Canvas getCanvas() {
        return drawCanvas;
    }

    public void setCanvas(Canvas n) {
        drawCanvas = n;
    }

    //this will be called when the user selects a different marker
    public void changeMarker(int m) {
        if (m == 4) {
            drawPaint.setAntiAlias(false);
        } else {
            drawPaint.setAntiAlias(true);
        }
        currentMarker = tray[m];
    }

    public Marker getCurrentMarker() {
        return currentMarker;
    }

    //these will be called when the user changes the properties of an existing marker
    public void changeMarkerColor(int color) {
        currentMarker.setColor(color);
    }

    public void changeMarkerWidth(int width) {
        currentMarker.setStrokeWidth(width);
    }

    public void addSelectionToCanvas(int x, int y) {
        drawCanvas.drawBitmap(selector.getReplacement(), originX, originY, null);
        drawCanvas.drawBitmap(selector.getSelection(), x, y, null);
        originX = x;
        originY = y;
        invalidate();
    }

    public void saveImage(String name, String description, Context context) {
        //Please add  <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
        //to manifest
        //Saves selection (as a bitmap) on the device
        //Code Referenced: http://stackoverflow.com/questions/16861753/android-saving-bitmap-to-phone-storage

        Bitmap temp = Bitmap.createBitmap(canvasBitmap.getWidth() * subBitmaps.size(), canvasBitmap.getHeight(), canvasBitmap.getConfig());
        temp.eraseColor(Color.WHITE);
        Canvas canvas = new Canvas(temp);
        float x = 0;
        for (int i = 0; i < subBitmaps.size(); i++) {
            canvas.drawBitmap(subBitmaps.get(i), x, 0f, null);
            x += (float) canvasBitmap.getWidth();
        }
        canvas.setBitmap(temp);

        MediaStore.Images.Media.insertImage(context.getContentResolver(), temp, name, description);

    }

    public void firstTimeSequence() {
        firstTimeDialog();
    }

    public void firstTimeDialog() {
        // Create custom dialog object
        final Dialog dialog = new Dialog(getContext());
        // Include dialog_size.xml file
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_help);

        dialog.show();

        Button getStarted = (Button) dialog.findViewById(R.id.button);

        // if decline button is clicked, close the custom dialog
        getStarted.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                askForName();
                dialog.dismiss();
            }
        });
    }

    public void helpDialog() {

        // Create custom dialog object
        final Dialog dialog = new Dialog(getContext());
        // Include dialog_size.xml file
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_help);

        dialog.show();

        Button getStarted = (Button) dialog.findViewById(R.id.button);

        // if decline button is clicked, close the custom dialog
        getStarted.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void askForName() {

        // Create custom dialog object
        final Dialog dialog = new Dialog(getContext());
        // Include dialog_size.xml file

        dialog.setContentView(R.layout.dialog_new_board);
        dialog.setTitle("You Started A New Board!");

        dialog.show();

        Button nameIt = (Button) dialog.findViewById(R.id.create);
        final EditText nameInput = (EditText) dialog.findViewById(R.id.board_name);
        final EditText descriptionInput = (EditText) dialog.findViewById(R.id.board_description);
        // if decline button is clicked, close the custom dialog
        nameIt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setName(nameInput.getText().toString());
                setDescription(descriptionInput.getText().toString());
                saveYourSelf();
                dialog.dismiss();
            }
        });
    }

    public void expandBoard() {
        colorFlags = new boolean[9];
        if (subBitmaps.indexOf(canvasBitmap) == subBitmaps.size() - 1 && subBitmaps.size() < LIMIT) {
            WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            System.out.println(canvasBitmap.getWidth());
            Bitmap newBoard = Bitmap.createBitmap(canvasBitmap.getWidth(), canvasBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Bitmap mutable = newBoard.copy(Bitmap.Config.ARGB_8888, true);
            subBitmaps.add(mutable);
            System.out.println(subBitmaps.size());
            RelativeLayout r = (RelativeLayout) (this.getParent()).getParent();
            Button rightArrow = (Button) r.findViewById(R.id.right_arrow);
            rightArrow.setVisibility(VISIBLE);
            Toast.makeText(this.getContext(), "Board Expanded.",
                    Toast.LENGTH_SHORT).show();
        } else {
            System.out.println("NO,NO,NO!");
        }
    }

    private String getTimeCreated() {
        Calendar c = Calendar.getInstance();
        return c.getTime().toString();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDateCreated() {
        return timeCreated.toString();
    }

    public void setName(String n) {
        name = n;
    }

    public void setDescription(String d) {
        description = d;
    }

    public void setTimeCreated(String d) {
        timeCreated = d;
    }

    public void goRightSubBoard() {
        int index = subBitmaps.indexOf(canvasBitmap);

        if (index != subBitmaps.size() - 1) {
            canvasBitmap = subBitmaps.get(index + 1);
            drawCanvas = new Canvas(canvasBitmap);
            invalidate();
            setArrowVisibility(index + 1);
        }


    }

    public void goLeftSubBoard() {
        int index = subBitmaps.indexOf(canvasBitmap);
        if (index != 0) {
            canvasBitmap = subBitmaps.get(subBitmaps.indexOf(canvasBitmap) - 1);
            drawCanvas = new Canvas(canvasBitmap);
            invalidate();
            setArrowVisibility(index - 1);
        }

    }

    public void goToSubBoard(int x) {
        canvasBitmap = subBitmaps.get(x);
        drawCanvas = new Canvas(canvasBitmap);
        setArrowVisibility(x);
        invalidate();
    }

    public boolean canExpand() {
        if (subBitmaps.indexOf(canvasBitmap) == subBitmaps.size() - 1 && subBitmaps.indexOf(canvasBitmap) + 1 != LIMIT)
            return true;
        return false;
    }

    public void createNewAnchor(String name) {
        anchors.add(new Anchor(subBitmaps.indexOf(canvasBitmap), name, this));
    }

    private void setArrowVisibility(int i) {
        RelativeLayout r = (RelativeLayout) (this.getParent()).getParent();
        Button rightArrow = (Button) r.findViewById(R.id.right_arrow);
        Button leftArrow = (Button) r.findViewById(R.id.left_arrow);

        if (i == 0 && subBitmaps.size() > 1) {
            leftArrow.setVisibility(GONE);
            rightArrow.setVisibility(VISIBLE);
        } else if (i != 0 && i != subBitmaps.size() - 1) {
            rightArrow.setVisibility(VISIBLE);
            leftArrow.setVisibility(VISIBLE);
        } else if (i == subBitmaps.size() - 1 && subBitmaps.size() > 1) {
            rightArrow.setVisibility(GONE);
            leftArrow.setVisibility(VISIBLE);
        } else {
            leftArrow.setVisibility(GONE);
            rightArrow.setVisibility(GONE);
        }
    }

    boolean colorFlags[] = new boolean[9];

    private void raiseFlag(float x, float y) {
        float subWidth = canvasBitmap.getWidth() / 3;
        float subHeight = canvasBitmap.getHeight() / 3;
        if (x >= 0 && x < subWidth && y >= 0 && y < subHeight) {
            colorFlags[0] = true;
        } else if (x >= subWidth && x < subWidth * 2 && y >= 0 && y < subHeight) {
            colorFlags[1] = true;
        } else if (x >= subWidth * 2 && x < subWidth * 3 && y >= 0 && y < subHeight) {
            colorFlags[2] = true;
        } else if (x >= 0 && x < subWidth && y >= subHeight && y < subHeight * 2) {
            colorFlags[3] = true;
        } else if (x >= subWidth && x < subWidth * 2 && y >= subHeight && y < subHeight * 2) {
            colorFlags[4] = true;
        } else if (x >= subWidth * 2 && x < subWidth * 3 && y >= subHeight && y < subHeight * 2) {
            colorFlags[5] = true;
        } else if (x >= 0 && x < subWidth && y >= subHeight * 2 && y < subHeight * 3) {
            colorFlags[6] = true;
        } else if (x >= subWidth && x < subWidth * 2 && y >= subHeight * 2 && y < subHeight * 3) {
            colorFlags[7] = true;
        } else if (x >= subWidth * 2 && x < subWidth * 3 && y >= subHeight * 2 && y < subHeight * 3) {
            colorFlags[8] = true;
        }

    }

    private boolean allFlagsTrue() {
        for (int i = 0; i < colorFlags.length; i++) {
            if (!colorFlags[i])
                return false;
        }
        return true;
    }

    public void saveYourSelf() {
        String boardDir = "/boards/board_" + getName();
        File board = new File(getContext().getFilesDir(), boardDir);

        if( board.exists())
            board.delete();

        if (!board.exists())
            board.mkdir();

        try {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("board");
            doc.appendChild(rootElement);

            // name elements
            Element name = doc.createElement("name");
            rootElement.appendChild(name);
            name.appendChild(doc.createTextNode(getName()));

            // description elements
            Element description = doc.createElement("description");
            rootElement.appendChild(description);
            description.appendChild(doc.createTextNode(getDescription()));

            // time created elements
            Element time = doc.createElement("timeCreated");
            rootElement.appendChild(time);
            time.appendChild(doc.createTextNode(getDateCreated()));

            for (int j = 0; j < tray.length; j++) {
                Element m = doc.createElement("marker");
                rootElement.appendChild(m);
                Element color = doc.createElement("color");
                color.appendChild(doc.createTextNode(Integer.toString(tray[j].getColor())));
                Element size = doc.createElement("size");
                size.appendChild(doc.createTextNode(Integer.toString(tray[j].getStrokeWidth())));
                m.appendChild(color);
                m.appendChild(size);
                m.setAttribute("id", Integer.toString(j));
            }

            for (int j = 0; j < subBitmaps.size(); j++) {
                Element sB = doc.createElement("subBitmap");
                rootElement.appendChild(sB);


                String filename = boardDir + "/subBitmap" + j + ".png";
                File file = new File(getContext().getFilesDir(), filename);



                try {
                    FileOutputStream out = new FileOutputStream(file);
                    subBitmaps.get(j).compress(Bitmap.CompressFormat.PNG, 90, out);
                    out.flush();
                    out.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.out.println("Saved SubBitmap.");


                Element fileName = doc.createElement("filename");
                fileName.appendChild(doc.createTextNode("subBitmap" + j + ".png"));
                sB.appendChild(fileName);
                sB.setAttribute("id", Integer.toString(j));
            }

            for (int j = 0; j < anchors.size(); j++) {
                Element a = doc.createElement("anchor");
                rootElement.appendChild(a);

                Element anchorname = doc.createElement("name");
                anchorname.appendChild(doc.createTextNode(anchors.get(j).getAnchorName()));
                a.appendChild(anchorname);
                Element pos = doc.createElement("pos");
                pos.appendChild(doc.createTextNode(Integer.toString(anchors.get(j).getAnchorPosX())));
                a.appendChild(pos);
                a.setAttribute("id", Integer.toString(j));
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(getContext().getFilesDir(), boardDir + "/props.xml"));

            // Output to console for testing
            //StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);

            try {

                String content = getName();
                String filename = "/boards/recent_board.txt";
                File file = new File(getContext().getFilesDir(), filename);

                // if file doesnt exists, then create it
                if (!file.exists()) {
                    file.createNewFile();
                }

                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(content);
                bw.close();

                System.out.println("Update Recent Board DONE.");

            } catch (IOException e) {
                e.printStackTrace();
            }
            addBoardNameToFile();
            System.out.println("File saved!");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }


    public void loadBoardByName(String name) {
        subBitmaps.clear();
        anchors.clear();
        try {

            File fXmlFile = new File(getContext().getFilesDir(), "/boards/board_" + name + "/props.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);


            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

            this.setName(doc.getElementsByTagName("name").item(0).getTextContent());
            this.setDescription(doc.getElementsByTagName("description").item(0).getTextContent());
            this.setTimeCreated(doc.getElementsByTagName("timeCreated").item(0).getTextContent());
            System.out.println("Name : " + doc.getElementsByTagName("name").item(0).getTextContent());
            System.out.println("Description : " + doc.getElementsByTagName("description").item(0).getTextContent());
            System.out.println("Time Created : " + doc.getElementsByTagName("timeCreated").item(0).getTextContent());

            NodeList markerList = doc.getElementsByTagName("marker");
            NodeList subBitmapList = doc.getElementsByTagName("subBitmap");
            NodeList anchorList = doc.getElementsByTagName("anchor");
            System.out.println("----------------------------");

            for (int j = 0; j < markerList.getLength(); j++) {


                Node nNode = markerList.item(j);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;
                    this.tray[j].setColor(Integer.parseInt(eElement.getElementsByTagName("color").item(0).getTextContent()));
                    this.tray[j].setStrokeWidth(Integer.parseInt(eElement.getElementsByTagName("size").item(0).getTextContent()));

                    System.out.println("Marker ID : " + eElement.getAttribute("id"));
                    System.out.println("Color : " + eElement.getElementsByTagName("color").item(0).getTextContent());
                    System.out.println("Size : " + eElement.getElementsByTagName("size").item(0).getTextContent());

                }
            }

            for (int j = 0; j < subBitmapList.getLength(); j++) {

                Node nNode = subBitmapList.item(j);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;

                    System.out.println("SubBitmap ID : " + eElement.getAttribute("id"));
                    System.out.println("File Name : " + eElement.getElementsByTagName("filename").item(0).getTextContent());
                    File f = new File(getContext().getFilesDir(), "/boards/board_" + name + "/" + eElement.getElementsByTagName("filename").item(0).getTextContent());
                    System.out.println(f);
                    Bitmap b = BitmapFactory.decodeFile(f.toString());

                    this.subBitmaps.add(b.copy(Bitmap.Config.ARGB_8888,true));


                }
            }

            for (int j = 0; j < anchorList.getLength(); j++) {

                Node nNode = anchorList.item(j);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;
                    this.anchors.add(new Anchor(Integer.parseInt(eElement.getElementsByTagName("pos").item(0).getTextContent()), eElement.getElementsByTagName("name").item(0).getTextContent(), this));
                    System.out.println("Anchor ID : " + eElement.getAttribute("id"));
                    System.out.println("Name : " + eElement.getElementsByTagName("name").item(0).getTextContent());
                    System.out.println("Positon : " + eElement.getElementsByTagName("pos").item(0).getTextContent());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Num Bitmap: " + subBitmaps.size());;
    }

    public void addBoardNameToFile(){
        try {

            String myName = getName();
            String filename = "/boards/board_names.txt";
            File file = new File(getContext().getFilesDir(), filename);
            String contents = "";
            boolean myNameReached = false;
            Scanner s = new Scanner(file);
            while (s.hasNext()) {
                String str = s.nextLine();
                if (str.equals(myName) && !myNameReached) {
                    contents = contents + myName +"\n";
                    myNameReached = true;
                } else {
                    contents = contents + str +"\n";
                }
            }

            if (!myNameReached){
                contents = contents + myName;
            }

            System.out.println(contents);
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(contents);
            bw.close();

            System.out.println("Added Name");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeBoardNameFromFile(){
        try {

            String myName = getName();
            String filename = "/boards/board_names.txt";
            File file = new File(getContext().getFilesDir(), filename);
            String contents = "";
            boolean myNameReached = false;
            Scanner s = new Scanner(file);
            while (s.hasNext()) {
                String str = s.nextLine();
                if (str.equals(myName) && !myNameReached) {
                    myNameReached = true;
                } else {
                    contents = contents + str +"\n";
                }
            }



            System.out.println(contents);
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(contents);
            bw.close();

            System.out.println("Removed Name");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteBoard(){
        removeBoardNameFromFile();
        String savedName = getName();
        try {
            String filename = "/boards/board_names.txt";
            File file = new File(getContext().getFilesDir(), filename);

            Scanner s = new Scanner(file);
            String str = s.nextLine();
            loadBoardByName(str);
            goToSubBoard(0);

        } catch (IOException e) {
            e.printStackTrace();
        }


            String filename = "/boards/board_"+ savedName;
            File file = new File(getContext().getFilesDir(), filename);

            file.delete();


        saveYourSelf();

    }

    public void createNewBoard(String n,String d){
        subBitmaps.clear();
        anchors.clear();

        canvasBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        subBitmaps.add(canvasBitmap);
        drawCanvas = new Canvas(canvasBitmap);

        this.name = n;
        this.description = d;
        this.timeCreated = getTimeCreated();
        //initializes the four markers for the tray
        this.tray[0] = new Marker(0xFF000000, 0);
        this.tray[1] = new Marker(0xFF009150, 1);
        this.tray[2] = new Marker(0xFF0070BB, 2);
        this.tray[3] = new Marker(0xFFDA2C43, 3);
        this.tray[4] = new Marker(0xFFffffff, 4, 45);
        this.currentMarker = tray[0];
        this.inSelector = false;
        this.inPasteMode = false;

        saveYourSelf();
        loadBoardByName(n);
    }

}




import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JComboBox;
import javax.swing.JProgressBar;
import java.awt.CardLayout;
import javax.swing.BoxLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.DefaultComboBoxModel;
import converters.ConvertSDBtoSDX;
import converters.ConvertSDXtoSDB;
import converters.LAMMPSDumpToSDX;
import converters.XYZToSDX;

/**
 * This provides a GUI frontend for the programs in this package.
 * @author Benjamin
 */
public class GUI extends JFrame {
	
	/**
	 * Version number.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The name of the card for all in one MSD calculations.
	 */
	protected static final String CARDMSD = "MSD - All In One";
	
	/**
	 * The name of the card for all in one VAC calculations.
	 */
	protected static final String CARDVAC = "VAC - All in One";
	
	/**
	 * The name of the card for file conversion operations.
	 */
	protected static final String CARDCONVERT = "Convert";
	
	/**
	 * The name of the card for performing computations.
	 */
	protected static final String CARDRUNNING = "Running";
	
	/**
	 * The name used for selecting the standard algorithm.
	 */
	protected static final String ALGSTANDARD = "Standard";
	
	/**
	 * The name used for selecting the one pass algorithm.
	 */
	protected static final String ALGONEPASS = "One Pass";
	
	/**
	 * The name used for selecting the multitau algorithm.
	 */
	protected static final String ALGMULTITAU = "Multitau";
	
	/**
	 * The name used for selecting the clip-repeat algorithm.
	 */
	protected static final String ALGCLIPREPEAT = "Clip-Repeat";
	
	/**
	 * The name of the card to convert SDB trajectories to SDX.
	 */
	protected static final String CONVSDBTOSDX = "SDB -> SDX";
	
	/**
	 * The name of the card to convert SDX trajectories to SDB.
	 */
	protected static final String CONVSDXTOSDB = "SDX -> SDB";
	
	/**
	 * The name of the card to convert lammps dump files to SDX.
	 */
	protected static final String CONVLAMMPSDUMPTOSDX = "Lammps Dump -> SDX";
	
	/**
	 * The name of the card to convert XYZ files to SDX.
	 */
	protected static final String CONVXYZTOSDX = "XYZ -> SDX";
	
	/**
	 * File chooser to use for browsing for source/destination files.
	 */
	protected JFileChooser fileChoose;
	
	/**
	 * File chooser to use for browsing for source/destination folders.
	 */
	protected JFileChooser folderChoose;
	
	/**The frame to show when running a command.*/
	protected JFrame running;
	
	/**The progress bar for activity.*/
	protected JProgressBar runningProg;
	
	/**The main viewing window.*/
	private JPanel contentPane;
	/**Scrollbars.*/
	protected JScrollPane scrollPane;
	/**The view selector.*/
	protected JPanel viewPanel;
	/**The card layout for viewPanel.*/
	protected CardLayout viewPanelLayout;
	/**The method to select what program to run.*/
	protected JComboBox<String> taskSel;
	/**The controls for all in one VAC calculations.*/
	protected JPanel VACPanel;
	/**The controls for all in one MSD calculations.*/
	protected JPanel MSDPanel;
	/**The controls for conversion operations.*/
	protected JPanel ConvertPanel;
	/**Used to select whether VAC should be calculated in RAM.*/
	protected JCheckBox vacInRAM;
	/**Selection for VAC algorithm.*/
	protected JComboBox<String> vacAlgSel;
	/**Panel for selecting source trajectory file.*/
	protected JPanel vacSourcePanel;
	/**Label for selection.*/
	protected JLabel vacSource;
	/**Field for selection.*/
	protected JTextField vacSourceTxt;
	/**Button to browse for selection.*/
	protected JButton vacSourceBrowse;
	/**Panel for selecting destination diffusion file.*/
	protected JPanel vacDestPanel;
	/**Label for selection.*/
	protected JLabel vacDest;
	/**Field for selection.*/
	protected JTextField vacDestTxt;
	/**Button to browse for selection.*/
	protected JButton vacDestBrowse;
	/**Label for selection.*/
	protected JLabel vacDist;
	/**Field to select distribution csv file.*/
	protected JTextField vacDistDestTxt;
	/**Button to browse for distribution.*/
	protected JButton vacDistDestBrowse;
	/**Panel for selecting working folder.*/
	protected JPanel vacWorkPanel;
	/**Label for selection.*/
	protected JLabel vacWork;
	/**Field for selection.*/
	protected JTextField vacWorkTxt;
	/**Button to browse for selection.*/
	protected JButton vacWorkBrowse;
	/**Panel to select the number of resamples.*/
	protected JPanel vacResampsPanel;
	/**Label for selection.*/
	protected JLabel vacNumResamps;
	/**Field for selection.*/
	protected JSpinner vacNumResampsSpin;
	/**Panel for selecting the algorithm to use.*/
	protected JPanel vacAlgPanel;
	/**Label for selection.*/
	protected JLabel vacAlg;
	/**Panel for selecting the number of dimensions in the trajectory.*/
	protected JPanel vacDimsPanel;
	/**Label for selection.*/
	protected JLabel vacDims;
	/**Field for selection.*/
	protected JSpinner vacDimsSpin;
	/**Button to start running the VAC program.*/
	protected JButton vacGo;
	/**Panel for go button.*/
	protected JPanel vacGoPanel;
	/**Panel for selecting multitau aggregation number.*/
	protected JPanel vacAggPanel;
	/**Label for selection.*/
	protected JLabel vacAgg;
	/**Panel for selecting clip-repeat step size.*/
	protected JPanel vacClipPanel;
	/**Label for selection.*/
	protected JLabel vacClip;
	/**Field for selection.*/
	protected JSpinner vacAggSpin;
	/**Field for selection.*/
	protected JSpinner vacClipSpin;
	
	/**Panel to select minimum variance.*/
	protected JPanel vacVarRatPanel;
	/**Label for minimum variance.*/
	protected JLabel vacVarRat;
	/**Spinner for minimum variance.*/
	protected JSpinner vacVarRatSpin;
	
	/**Panel for selecting MSD algorithm.*/
	protected JPanel msdAlgPanel;
	/**Label for selection.*/
	protected JLabel msdAlg;
	/**Used to select whether MSD should be calculated in RAM.*/
	protected JCheckBox msdInRAM;
	/**Field for selection.*/
	protected JComboBox<String> msdAlgSel;
	/**Panel for selecting source file for MSD.*/
	protected JPanel msdSourcePanel;
	/**Panel for selecting destination file for MSD.*/
	protected JPanel msdDestPanel;
	/**Panel for selecting the working directory for MSD.*/
	protected JPanel msdWorkPanel;
	/**Label for selection.*/
	protected JLabel msdSource;
	/**Label for selection.*/
	protected JLabel msdDest;
	/**Label for selection.*/
	protected JLabel msdDist;
	/**Label for selection.*/
	protected JLabel msdWork;
	/**Field for selection.*/
	protected JTextField msdSourceTxt;
	/**Field for selection.*/
	protected JTextField msdDestTxt;
	/**Field for selection.*/
	protected JTextField msdWorkTxt;
	/**Button to browse for selection.*/
	protected JButton msdSourceBrowse;
	/**Button to browse for selection.*/
	protected JButton msdDestBrowse;
	/**Field to select distribution csv file.*/
	protected JTextField msdDistDestTxt;
	/**Button to browse for distribution.*/
	protected JButton msdDistDestBrowse;
	/**Button to browse for selection.*/
	protected JButton msdWorkBrowse;
	/**Panel for selecting number of resamples for MSD.*/
	protected JPanel msdResampPanel;
	/**Label for selection.*/
	protected JLabel msdResamp;
	/**Field for selection.*/
	protected JSpinner msdResampSpin;
	/**Panel for selecting the number of dimensions in the problem for MSD.*/
	protected JPanel msdDimsPanel;
	/**Label for selection.*/
	protected JLabel msdDims;
	/**Field for selection.*/
	protected JSpinner msdDimsSpin;
	/**Panel for selecting aggregation number for multitau.*/
	protected JPanel msdAggPanel;
	/**Label for selection.*/
	protected JLabel msdAgg;
	/**Field for selection.*/
	protected JSpinner msdAggSpin;
	/**Panel for selecting clip-repeat length.*/
	protected JPanel msdClipPanel;
	/**Label for selection.*/
	protected JLabel msdClip;
	/**Field for selection.*/
	protected JSpinner msdClipSpin;
	/**Button to make MSD go.*/
	protected JPanel msdGoPanel;
	/**Button to make MSD go.*/
	protected JButton msdGo;
	/**Panel to select maximum curvature at start.*/
	protected JPanel msdCurvePanel;
	/**Label for curvature.*/
	protected JLabel msdCurve;
	/**Panel for selecting minimum signal to noise at the end.*/
	protected JPanel msdSTNPanel;
	/**Label for signal to noise.*/
	protected JLabel msdSTN;
	/**Spinner for max curvature.*/
	protected JSpinner msdCurveSpin;
	/**Spinner for min signal to noise.*/
	protected JSpinner msdSTNSpin;
	/**Method to select what to convert to what.*/
	protected JComboBox<String> conFmtSel;
	/**Card panel for format conversion.*/
	protected JPanel formCardPanel;
	/**The card layout for formCardPanel.*/
	protected CardLayout formCardLayout;
	/**Panel for converting SDX trajectories to SDB.*/
	protected JPanel conSDXSDB;
	/**Panel for converting SDB trajectories to SDX.*/
	protected JPanel conSDBSDX;
	/**Panel for converting lammps dumps to SDX.*/
	protected JPanel conLammpsSDX;
	/**Panel for converting XYZ to SDX.*/
	protected JPanel conXYZSDX;
	/**Label for selection.*/
	protected JLabel conSDXSDBSource;
	/**Field for selection.*/
	protected JTextField conSDXSDBSourceTxt;
	/**Button to browse for selection.*/
	protected JButton conSDXSDBSourceBrowse;
	/**Panel to select source.*/
	protected JPanel conSDXSDBSourcePanel;
	/**Panel to select destination.*/
	protected JPanel conSDXSDBDestPanel;
	/**Label for selection.*/
	protected JLabel conSDXSDBDest;
	/**Field for selection.*/
	protected JTextField conSDXSDBDestTxt;
	/**Button to browse for selection.*/
	protected JButton conSDXSDBDestBrowse;
	/**Button to start conversion.*/
	protected JButton conSDXSDBConvButton;
	/**Panel for selection.*/
	protected JPanel conSDBSDXSourcePanel;
	/**Label for selection.*/
	protected JLabel conSDBSDXSource;
	/**Field for selection.*/
	protected JTextField conSDBSDXSourceTxt;
	/**Button to browse for selection.*/
	protected JButton conSDBSDXSourceBrowse;
	/**Panel for selection.*/
	protected JPanel conSDBSDXDestPanel;
	/**Label for selection.*/
	protected JLabel conSDBSDXDest;
	/**Field for selection.*/
	protected JTextField conSDBSDXDestTxt;
	/**Button to browse for selection.*/
	protected JButton conSDBSDXDestBrowse;
	/**Button to start conversion.*/
	protected JButton conSDBSDXConvButton;
	/**Panel for selection.*/
	protected JPanel conLammpsSDXSourcePanel;
	/**Label for selection.*/
	protected JLabel conLammpsSDXSource;
	/**Field for selection.*/
	protected JTextField conLammpsSDXSourceTxt;
	/**Button to browse for selection.*/
	protected JButton conLammpsSDXSourceBrowse;
	/**Panel for selection.*/
	protected JPanel conLammpsSDXDestPanel;
	/**Label for selection.*/
	protected JLabel conLammpsSDXDest;
	/**Field for selection.*/
	protected JTextField conLammpsSDXDestTxt;
	/**Button to browse for selection.*/
	protected JButton conLammpsSDXDestBrowse;
	/**Button to start conversion.*/
	protected JButton conLammpsSDXConvButton;
	/**Panel for selection.*/
	protected JPanel conLammpsSDXDimPanel;
	/**Label for selection.*/
	protected JLabel conLammpsSDXDim;
	/**Field for selection.*/
	protected JSpinner conLammpsSDXDimSpin;
	/**Panel for selection.*/
	protected JPanel conLammpsSDXTimePanel;
	/**Label for selection.*/
	protected JLabel conLammpsSDXTime;
	/**Field for selection.*/
	protected JSpinner conLammpsSDXTimeSpin;
	
	/**Panel for selection.*/
	protected JPanel conXYZSDXSourcePanel;
	/**Label for selection.*/
	protected JLabel conXYZSDXSource;
	/**Field for selection.*/
	protected JTextField conXYZSDXSourceTxt;
	/**Button to browse for selection.*/
	protected JButton conXYZSDXSourceBrowse;
	/**Panel for selection.*/
	protected JPanel conXYZSDXDestPanel;
	/**Label for selection.*/
	protected JLabel conXYZSDXDest;
	/**Field for selection.*/
	protected JTextField conXYZSDXDestTxt;
	/**Button to browse for selection.*/
	protected JButton conXYZSDXDestBrowse;
	/**Button to start conversion.*/
	protected JButton conXYZSDXConvButton;
	/**Panel for selection.*/
	protected JPanel conXYZSDXDimPanel;
	/**Label for selection.*/
	protected JLabel conXYZSDXDim;
	/**Field for selection.*/
	protected JSpinner conXYZSDXDimSpin;
	/**Panel for selection.*/
	protected JPanel conXYZSDXTimePanel;
	/**Label for selection.*/
	protected JLabel conXYZSDXTime;
	/**Field for selection.*/
	protected JSpinner conXYZSDXTimeSpin;
	/**The panel to show when running.*/
	protected JPanel runningPanel;
	/**The label used to note an error.*/
	protected JLabel runningError;

	/**
	 * Launch the application.
	 * @param args Ignored.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI frame = new GUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public GUI() {
		setTitle("DiffResJ");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 500, 450);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		fileChoose = new JFileChooser();
		fileChoose.setFileSelectionMode(JFileChooser.FILES_ONLY);
		
		folderChoose = new JFileChooser();
		folderChoose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		viewPanel = new JPanel();
		scrollPane.setViewportView(viewPanel);
		viewPanelLayout = new CardLayout(0, 0);
		viewPanel.setLayout(viewPanelLayout);
		
		VACPanel = new JPanel();
		viewPanel.add(VACPanel, CARDVAC);
		VACPanel.setLayout(new BoxLayout(VACPanel, BoxLayout.Y_AXIS));
		
		vacInRAM = new JCheckBox("In RAM?", true);
		VACPanel.add(vacInRAM);
		
		vacAlgPanel = new JPanel();
		VACPanel.add(vacAlgPanel);
		vacAlgPanel.setLayout(new GridLayout(0, 2, 0, 0));
		
		vacAlg = new JLabel("Algorithm");
		vacAlg.setHorizontalAlignment(SwingConstants.CENTER);
		vacAlgPanel.add(vacAlg);
		
		vacAlgSel = new JComboBox<String>();
		vacAlgSel.setModel(new DefaultComboBoxModel<String>(new String[] {ALGSTANDARD, ALGONEPASS, ALGMULTITAU, ALGCLIPREPEAT}));
		vacAlgPanel.add(vacAlgSel);
		
		vacSourcePanel = new JPanel();
		VACPanel.add(vacSourcePanel);
		vacSourcePanel.setLayout(new GridLayout(0, 3, 0, 0));
		
		vacSource = new JLabel("Source (mdp.sdb)");
		vacSource.setHorizontalAlignment(SwingConstants.CENTER);
		vacSourcePanel.add(vacSource);
		
		vacSourceTxt = new JTextField();
		vacSourcePanel.add(vacSourceTxt);
		vacSourceTxt.setColumns(10);
		
		vacSourceBrowse = new JButton("Browse");
		vacSourceBrowse.addActionListener(new BrowseFileListener(vacSourceTxt, fileChoose));
		vacSourcePanel.add(vacSourceBrowse);
		
		vacDestPanel = new JPanel();
		VACPanel.add(vacDestPanel);
		vacDestPanel.setLayout(new GridLayout(0, 3, 0, 0));
		
		vacDest = new JLabel("Destination (dif.sdx)");
		vacDest.setHorizontalAlignment(SwingConstants.CENTER);
		vacDestPanel.add(vacDest);
		
		vacDestTxt = new JTextField();
		vacDestPanel.add(vacDestTxt);
		vacDestTxt.setColumns(10);
		
		vacDestBrowse = new JButton("Browse");
		vacDestBrowse.addActionListener(new BrowseFileListener(vacDestTxt, fileChoose));
		vacDestPanel.add(vacDestBrowse);
		
		vacDist = new JLabel("Distribution (csv)");
		vacDist.setHorizontalAlignment(SwingConstants.CENTER);
		vacDestPanel.add(vacDist);
		
		vacDistDestTxt = new JTextField();
		vacDestPanel.add(vacDistDestTxt);
		vacDistDestTxt.setColumns(10);
		
		vacDistDestBrowse = new JButton("Browse");
		vacDistDestBrowse.addActionListener(new BrowseFileListener(vacDistDestTxt, fileChoose));
		vacDestPanel.add(vacDistDestBrowse);
		
		vacWorkPanel = new JPanel();
		VACPanel.add(vacWorkPanel);
		vacWorkPanel.setLayout(new GridLayout(0, 3, 0, 0));
		
		vacWork = new JLabel("Working Folder");
		vacWork.setHorizontalAlignment(SwingConstants.CENTER);
		vacWorkPanel.add(vacWork);
		
		vacWorkTxt = new JTextField();
		vacWorkPanel.add(vacWorkTxt);
		vacWorkTxt.setColumns(10);
		
		vacWorkBrowse = new JButton("Browse");
		vacWorkBrowse.addActionListener(new BrowseFileListener(vacWorkTxt, folderChoose));
		vacWorkPanel.add(vacWorkBrowse);
		
		vacResampsPanel = new JPanel();
		VACPanel.add(vacResampsPanel);
		vacResampsPanel.setLayout(new GridLayout(0, 2, 0, 0));
		
		vacNumResamps = new JLabel("Number of Resamples");
		vacNumResamps.setHorizontalAlignment(SwingConstants.CENTER);
		vacResampsPanel.add(vacNumResamps);
		
		vacNumResampsSpin = new JSpinner();
		vacNumResampsSpin.setModel(new SpinnerNumberModel(new Integer(100), new Integer(3), null, new Integer(1)));
		vacResampsPanel.add(vacNumResampsSpin);
		
		vacDimsPanel = new JPanel();
		VACPanel.add(vacDimsPanel);
		vacDimsPanel.setLayout(new GridLayout(0, 2, 0, 0));
		
		vacDims = new JLabel("Number of Dimensions");
		vacDims.setHorizontalAlignment(SwingConstants.CENTER);
		vacDimsPanel.add(vacDims);
		
		vacDimsSpin = new JSpinner();
		vacDimsSpin.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		vacDimsPanel.add(vacDimsSpin);
		
		vacVarRatPanel = new JPanel();
		VACPanel.add(vacVarRatPanel);
		vacVarRatPanel.setLayout(new GridLayout(0, 2, 0, 0));
		
		vacVarRat = new JLabel("Integral Cutoff Ratio");
		vacVarRat.setHorizontalAlignment(SwingConstants.CENTER);
		vacVarRatPanel.add(vacVarRat);
		
		vacVarRatSpin = new JSpinner();
		vacVarRatSpin.setModel(new SpinnerNumberModel(new Double(0.05), new Double(0), new Double(1), new Double(0.01)));
		vacVarRatPanel.add(vacVarRatSpin);
		
		vacAggPanel = new JPanel();
		VACPanel.add(vacAggPanel);
		vacAggPanel.setLayout(new GridLayout(0, 2, 0, 0));
		
		vacAgg = new JLabel("Aggregation Number");
		vacAgg.setEnabled(false);
		vacAgg.setHorizontalAlignment(SwingConstants.CENTER);
		vacAggPanel.add(vacAgg);
		
		vacAggSpin = new JSpinner();
		vacAggSpin.setEnabled(false);
		vacAggSpin.setModel(new SpinnerNumberModel(new Integer(2), new Integer(2), null, new Integer(1)));
		vacAggPanel.add(vacAggSpin);
		
		vacClipPanel = new JPanel();
		VACPanel.add(vacClipPanel);
		vacClipPanel.setLayout(new GridLayout(0, 2, 0, 0));
		
		vacClip = new JLabel("Timestep Clip");
		vacClip.setEnabled(false);
		vacClip.setHorizontalAlignment(SwingConstants.CENTER);
		vacClipPanel.add(vacClip);
		
		vacClipSpin = new JSpinner();
		vacClipSpin.setEnabled(false);
		vacClipSpin.setModel(new SpinnerNumberModel(new Integer(2), new Integer(2), null, new Integer(1)));
		vacClipPanel.add(vacClipSpin);
		vacAlgSel.addActionListener(new AlgorithmSelectionListener(vacAlgSel, vacClipSpin, vacAggSpin));
		
		vacGoPanel = new JPanel();
		VACPanel.add(vacGoPanel);
		vacGoPanel.setLayout(new GridLayout(0, 1, 0, 0));
		
		vacGo = new JButton("Go");
		vacGo.addActionListener(new VACGoListener());
		vacGoPanel.add(vacGo);
		
		MSDPanel = new JPanel();
		viewPanel.add(MSDPanel, CARDMSD);
		MSDPanel.setLayout(new BoxLayout(MSDPanel, BoxLayout.Y_AXIS));
		
		msdInRAM = new JCheckBox("In RAM?", true);
		MSDPanel.add(msdInRAM);
		
		msdAlgPanel = new JPanel();
		MSDPanel.add(msdAlgPanel);
		msdAlgPanel.setLayout(new GridLayout(0, 2, 0, 0));
		
		msdAlg = new JLabel("Algorithm");
		msdAlgPanel.add(msdAlg);
		
		msdAlgSel = new JComboBox<String>();
		msdAlgSel.setModel(new DefaultComboBoxModel<String>(new String[] {ALGSTANDARD, ALGONEPASS, ALGMULTITAU, ALGCLIPREPEAT}));
		msdAlgPanel.add(msdAlgSel);
		
		msdSourcePanel = new JPanel();
		MSDPanel.add(msdSourcePanel);
		msdSourcePanel.setLayout(new GridLayout(0, 3, 0, 0));
		
		msdSource = new JLabel("Source");
		msdSource.setHorizontalAlignment(SwingConstants.CENTER);
		msdSourcePanel.add(msdSource);
		
		msdSourceTxt = new JTextField();
		msdSourcePanel.add(msdSourceTxt);
		msdSourceTxt.setColumns(10);
		
		msdSourceBrowse = new JButton("Browse");
		msdSourceBrowse.addActionListener(new BrowseFileListener(msdSourceTxt, fileChoose));
		msdSourcePanel.add(msdSourceBrowse);
		
		msdDestPanel = new JPanel();
		MSDPanel.add(msdDestPanel);
		msdDestPanel.setLayout(new GridLayout(0, 3, 0, 0));
		
		msdDest = new JLabel("Destination");
		msdDest.setHorizontalAlignment(SwingConstants.CENTER);
		msdDestPanel.add(msdDest);
		
		msdDestTxt = new JTextField();
		msdDestPanel.add(msdDestTxt);
		msdDestTxt.setColumns(10);
		
		msdDestBrowse = new JButton("Browse");
		msdDestBrowse.addActionListener(new BrowseFileListener(msdDestTxt, fileChoose));
		msdDestPanel.add(msdDestBrowse);
		
		msdDist = new JLabel("Distribution (csv)");
		msdDist.setHorizontalAlignment(SwingConstants.CENTER);
		msdDestPanel.add(msdDist);
		
		msdDistDestTxt = new JTextField();
		msdDestPanel.add(msdDistDestTxt);
		msdDistDestTxt.setColumns(10);
		
		msdDistDestBrowse = new JButton("Browse");
		msdDistDestBrowse.addActionListener(new BrowseFileListener(msdDistDestTxt, fileChoose));
		msdDestPanel.add(msdDistDestBrowse);
		
		msdWorkPanel = new JPanel();
		MSDPanel.add(msdWorkPanel);
		msdWorkPanel.setLayout(new GridLayout(0, 3, 0, 0));
		
		msdWork = new JLabel("Working Folder");
		msdWork.setHorizontalAlignment(SwingConstants.CENTER);
		msdWorkPanel.add(msdWork);
		
		msdWorkTxt = new JTextField();
		msdWorkPanel.add(msdWorkTxt);
		msdWorkTxt.setColumns(10);
		
		msdWorkBrowse = new JButton("Browse");
		msdWorkBrowse.addActionListener(new BrowseFileListener(msdWorkTxt, folderChoose));
		msdWorkPanel.add(msdWorkBrowse);
		
		msdResampPanel = new JPanel();
		MSDPanel.add(msdResampPanel);
		msdResampPanel.setLayout(new GridLayout(0, 2, 0, 0));
		
		msdResamp = new JLabel("Number of Resamples");
		msdResamp.setHorizontalAlignment(SwingConstants.CENTER);
		msdResampPanel.add(msdResamp);
		
		msdResampSpin = new JSpinner();
		msdResampSpin.setModel(new SpinnerNumberModel(new Integer(100), new Integer(3), null, new Integer(1)));
		msdResampPanel.add(msdResampSpin);
		
		msdDimsPanel = new JPanel();
		MSDPanel.add(msdDimsPanel);
		msdDimsPanel.setLayout(new GridLayout(0, 2, 0, 0));
		
		msdDims = new JLabel("Number of Dimensions");
		msdDims.setHorizontalAlignment(SwingConstants.CENTER);
		msdDimsPanel.add(msdDims);
		
		msdDimsSpin = new JSpinner();
		msdDimsSpin.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		msdDimsPanel.add(msdDimsSpin);
		
		msdCurvePanel = new JPanel();
		MSDPanel.add(msdCurvePanel);
		msdCurvePanel.setLayout(new GridLayout(0, 2, 0, 0));
		
		msdCurve = new JLabel("Maximum Curvature");
		msdCurve.setHorizontalAlignment(SwingConstants.CENTER);
		msdCurvePanel.add(msdCurve);
		
		msdCurveSpin = new JSpinner();
		msdCurveSpin.setModel(new SpinnerNumberModel(new Double(0.01), new Double(0), null, new Double(1)));
		msdCurvePanel.add(msdCurveSpin);
		
		msdSTNPanel = new JPanel();
		MSDPanel.add(msdSTNPanel);
		msdSTNPanel.setLayout(new GridLayout(0, 2, 0, 0));
		
		msdSTN = new JLabel("Minimum Signal to Noise");
		msdSTN.setHorizontalAlignment(SwingConstants.CENTER);
		msdSTNPanel.add(msdSTN);
		
		msdSTNSpin = new JSpinner();
		msdSTNSpin.setModel(new SpinnerNumberModel(new Double(0), new Double(0), null, new Double(1)));
		msdSTNPanel.add(msdSTNSpin);
		
		msdAggPanel = new JPanel();
		MSDPanel.add(msdAggPanel);
		msdAggPanel.setLayout(new GridLayout(0, 2, 0, 0));
		
		msdAgg = new JLabel("Aggregation Number");
		msdAgg.setHorizontalAlignment(SwingConstants.CENTER);
		msdAgg.setEnabled(false);
		msdAggPanel.add(msdAgg);
		
		msdAggSpin = new JSpinner();
		msdAggSpin.setModel(new SpinnerNumberModel(new Integer(2), new Integer(2), null, new Integer(1)));
		msdAggSpin.setEnabled(false);
		msdAggPanel.add(msdAggSpin);
		
		msdClipPanel = new JPanel();
		MSDPanel.add(msdClipPanel);
		msdClipPanel.setLayout(new GridLayout(0, 2, 0, 0));
		
		msdClip = new JLabel("Timestep Clip");
		msdClip.setHorizontalAlignment(SwingConstants.CENTER);
		msdClip.setEnabled(false);
		msdClipPanel.add(msdClip);
		
		msdClipSpin = new JSpinner();
		msdClipSpin.setModel(new SpinnerNumberModel(new Integer(2), new Integer(2), null, new Integer(1)));
		msdClipSpin.setEnabled(false);
		msdClipPanel.add(msdClipSpin);
		msdAlgSel.addActionListener(new AlgorithmSelectionListener(msdAlgSel, msdClipSpin, msdAggSpin));
		
		msdGoPanel = new JPanel();
		MSDPanel.add(msdGoPanel);
		msdGoPanel.setLayout(new GridLayout(0, 1, 0, 0));
		
		msdGo = new JButton("Go");
		msdGo.addActionListener(new MSDGoListener());
		msdGoPanel.add(msdGo);
		
		ConvertPanel = new JPanel();
		viewPanel.add(ConvertPanel, CARDCONVERT);
		ConvertPanel.setLayout(new BorderLayout(0, 0));
		
		conFmtSel = new JComboBox<String>();
		conFmtSel.setModel(new DefaultComboBoxModel<String>(new String[] {CONVSDXTOSDB, CONVSDBTOSDX, CONVLAMMPSDUMPTOSDX, CONVXYZTOSDX}));
		ConvertPanel.add(conFmtSel, BorderLayout.NORTH);
		
		formCardPanel = new JPanel();
		ConvertPanel.add(formCardPanel, BorderLayout.CENTER);
		formCardLayout = new CardLayout(0, 0);
		formCardPanel.setLayout(formCardLayout);
		conFmtSel.addActionListener(new TaskSelectionListener(conFmtSel, formCardLayout, formCardPanel));
		
		conSDXSDB = new JPanel();
		formCardPanel.add(conSDXSDB, CONVSDXTOSDB);
		conSDXSDB.setLayout(new BoxLayout(conSDXSDB, BoxLayout.Y_AXIS));
		
		conSDXSDBSourcePanel = new JPanel();
		conSDXSDB.add(conSDXSDBSourcePanel);
		conSDXSDBSourcePanel.setLayout(new GridLayout(0, 3, 0, 0));
		
		conSDXSDBSource = new JLabel("Source");
		conSDXSDBSource.setHorizontalAlignment(SwingConstants.CENTER);
		conSDXSDBSourcePanel.add(conSDXSDBSource);
		
		conSDXSDBSourceTxt = new JTextField();
		conSDXSDBSourcePanel.add(conSDXSDBSourceTxt);
		conSDXSDBSourceTxt.setColumns(10);
		
		conSDXSDBSourceBrowse = new JButton("Browse");
		conSDXSDBSourceBrowse.addActionListener(new BrowseFileListener(conSDXSDBSourceTxt, fileChoose));
		conSDXSDBSourcePanel.add(conSDXSDBSourceBrowse);
		
		conSDXSDBDestPanel = new JPanel();
		conSDXSDB.add(conSDXSDBDestPanel);
		conSDXSDBDestPanel.setLayout(new GridLayout(0, 3, 0, 0));
		
		conSDXSDBDest = new JLabel("Destination");
		conSDXSDBDest.setHorizontalAlignment(SwingConstants.CENTER);
		conSDXSDBDestPanel.add(conSDXSDBDest);
		
		conSDXSDBDestTxt = new JTextField();
		conSDXSDBDestPanel.add(conSDXSDBDestTxt);
		conSDXSDBDestTxt.setColumns(10);
		
		conSDXSDBDestBrowse = new JButton("Browse");
		conSDXSDBDestBrowse.addActionListener(new BrowseFileListener(conSDXSDBDestTxt, fileChoose));
		conSDXSDBDestPanel.add(conSDXSDBDestBrowse);
		
		conSDXSDBConvButton = new JButton("Convert");
		conSDXSDBConvButton.addActionListener(new SDXSDBGoListener());
		conSDXSDB.add(conSDXSDBConvButton);
		
		conSDBSDX = new JPanel();
		formCardPanel.add(conSDBSDX, CONVSDBTOSDX);
		conSDBSDX.setLayout(new BoxLayout(conSDBSDX, BoxLayout.Y_AXIS));
		
		conSDBSDXSourcePanel = new JPanel();
		conSDBSDX.add(conSDBSDXSourcePanel);
		conSDBSDXSourcePanel.setLayout(new GridLayout(0, 3, 0, 0));
		
		conSDBSDXSource = new JLabel("Source");
		conSDBSDXSource.setHorizontalAlignment(SwingConstants.CENTER);
		conSDBSDXSourcePanel.add(conSDBSDXSource);
		
		conSDBSDXSourceTxt = new JTextField();
		conSDBSDXSourceTxt.setColumns(10);
		conSDBSDXSourcePanel.add(conSDBSDXSourceTxt);
		
		conSDBSDXSourceBrowse = new JButton("Browse");
		conSDBSDXSourceBrowse.addActionListener(new BrowseFileListener(conSDBSDXSourceTxt, fileChoose));
		conSDBSDXSourcePanel.add(conSDBSDXSourceBrowse);
		
		conSDBSDXDestPanel = new JPanel();
		conSDBSDX.add(conSDBSDXDestPanel);
		conSDBSDXDestPanel.setLayout(new GridLayout(0, 3, 0, 0));
		
		conSDBSDXDest = new JLabel("Destination");
		conSDBSDXDest.setHorizontalAlignment(SwingConstants.CENTER);
		conSDBSDXDestPanel.add(conSDBSDXDest);
		
		conSDBSDXDestTxt = new JTextField();
		conSDBSDXDestTxt.setColumns(10);
		conSDBSDXDestPanel.add(conSDBSDXDestTxt);
		
		conSDBSDXDestBrowse = new JButton("Browse");
		conSDBSDXDestBrowse.addActionListener(new BrowseFileListener(conSDBSDXDestTxt, fileChoose));
		conSDBSDXDestPanel.add(conSDBSDXDestBrowse);
		
		conSDBSDXConvButton = new JButton("Convert");
		conSDBSDXConvButton.addActionListener(new SDBSDXGoListener());
		conSDBSDX.add(conSDBSDXConvButton);
		
		conLammpsSDX = new JPanel();
		formCardPanel.add(conLammpsSDX, CONVLAMMPSDUMPTOSDX);
		conLammpsSDX.setLayout(new BoxLayout(conLammpsSDX, BoxLayout.Y_AXIS));
		
		conLammpsSDXSourcePanel = new JPanel();
		conLammpsSDX.add(conLammpsSDXSourcePanel);
		conLammpsSDXSourcePanel.setLayout(new GridLayout(0, 3, 0, 0));
		
		conLammpsSDXSource = new JLabel("Source");
		conLammpsSDXSource.setHorizontalAlignment(SwingConstants.CENTER);
		conLammpsSDXSourcePanel.add(conLammpsSDXSource);
		
		conLammpsSDXSourceTxt = new JTextField();
		conLammpsSDXSourceTxt.setColumns(10);
		conLammpsSDXSourcePanel.add(conLammpsSDXSourceTxt);
		
		conLammpsSDXSourceBrowse = new JButton("Browse");
		conLammpsSDXSourceBrowse.addActionListener(new BrowseFileListener(conLammpsSDXSourceTxt, fileChoose));
		conLammpsSDXSourcePanel.add(conLammpsSDXSourceBrowse);
		
		conLammpsSDXDestPanel = new JPanel();
		conLammpsSDX.add(conLammpsSDXDestPanel);
		conLammpsSDXDestPanel.setLayout(new GridLayout(0, 3, 0, 0));
		
		conLammpsSDXDest = new JLabel("Destination");
		conLammpsSDXDest.setHorizontalAlignment(SwingConstants.CENTER);
		conLammpsSDXDestPanel.add(conLammpsSDXDest);
		
		conLammpsSDXDestTxt = new JTextField();
		conLammpsSDXDestTxt.setColumns(10);
		conLammpsSDXDestPanel.add(conLammpsSDXDestTxt);
		
		conLammpsSDXDestBrowse = new JButton("Browse");
		conLammpsSDXDestBrowse.addActionListener(new BrowseFileListener(conLammpsSDXDestTxt, fileChoose));
		conLammpsSDXDestPanel.add(conLammpsSDXDestBrowse);
		
		conLammpsSDXDimPanel = new JPanel();
		conLammpsSDX.add(conLammpsSDXDimPanel);
		conLammpsSDXDimPanel.setLayout(new GridLayout(0, 2, 0, 0));
		
		conLammpsSDXDim = new JLabel("Dimension");
		conLammpsSDXDim.setHorizontalAlignment(SwingConstants.CENTER);
		conLammpsSDXDimPanel.add(conLammpsSDXDim);
		
		conLammpsSDXDimSpin = new JSpinner();
		conLammpsSDXDimSpin.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		conLammpsSDXDimPanel.add(conLammpsSDXDimSpin);
		
		conLammpsSDXTimePanel = new JPanel();
		conLammpsSDX.add(conLammpsSDXTimePanel);
		conLammpsSDXTimePanel.setLayout(new GridLayout(0, 2, 0, 0));
		
		conLammpsSDXTime = new JLabel("Timestep");
		conLammpsSDXTime.setHorizontalAlignment(SwingConstants.CENTER);
		conLammpsSDXTimePanel.add(conLammpsSDXTime);
		
		conLammpsSDXTimeSpin = new JSpinner();
		conLammpsSDXTimeSpin.setModel(new SpinnerNumberModel(new Double(0.01), new Double(0), null, new Double(1)));
		conLammpsSDXTimePanel.add(conLammpsSDXTimeSpin);
		
		conLammpsSDXConvButton = new JButton("Convert");
		conLammpsSDXConvButton.addActionListener(new LammpsSDXGoListener());
		conLammpsSDX.add(conLammpsSDXConvButton);
		
		
		
		conXYZSDX = new JPanel();
		formCardPanel.add(conXYZSDX, CONVXYZTOSDX);
		conXYZSDX.setLayout(new BoxLayout(conXYZSDX, BoxLayout.Y_AXIS));
		
		conXYZSDXSourcePanel = new JPanel();
		conXYZSDX.add(conXYZSDXSourcePanel);
		conXYZSDXSourcePanel.setLayout(new GridLayout(0, 3, 0, 0));
		
		conXYZSDXSource = new JLabel("Source");
		conXYZSDXSource.setHorizontalAlignment(SwingConstants.CENTER);
		conXYZSDXSourcePanel.add(conXYZSDXSource);
		
		conXYZSDXSourceTxt = new JTextField();
		conXYZSDXSourceTxt.setColumns(10);
		conXYZSDXSourcePanel.add(conXYZSDXSourceTxt);
		
		conXYZSDXSourceBrowse = new JButton("Browse");
		conXYZSDXSourceBrowse.addActionListener(new BrowseFileListener(conXYZSDXSourceTxt, fileChoose));
		conXYZSDXSourcePanel.add(conXYZSDXSourceBrowse);
		
		conXYZSDXDestPanel = new JPanel();
		conXYZSDX.add(conXYZSDXDestPanel);
		conXYZSDXDestPanel.setLayout(new GridLayout(0, 3, 0, 0));
		
		conXYZSDXDest = new JLabel("Destination");
		conXYZSDXDest.setHorizontalAlignment(SwingConstants.CENTER);
		conXYZSDXDestPanel.add(conXYZSDXDest);
		
		conXYZSDXDestTxt = new JTextField();
		conXYZSDXDestTxt.setColumns(10);
		conXYZSDXDestPanel.add(conXYZSDXDestTxt);
		
		conXYZSDXDestBrowse = new JButton("Browse");
		conXYZSDXDestBrowse.addActionListener(new BrowseFileListener(conXYZSDXDestTxt, fileChoose));
		conXYZSDXDestPanel.add(conXYZSDXDestBrowse);
		
		conXYZSDXDimPanel = new JPanel();
		conXYZSDX.add(conXYZSDXDimPanel);
		conXYZSDXDimPanel.setLayout(new GridLayout(0, 2, 0, 0));
		
		conXYZSDXDim = new JLabel("Dimension");
		conXYZSDXDim.setHorizontalAlignment(SwingConstants.CENTER);
		conXYZSDXDimPanel.add(conXYZSDXDim);
		
		conXYZSDXDimSpin = new JSpinner();
		conXYZSDXDimSpin.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		conXYZSDXDimPanel.add(conXYZSDXDimSpin);
		
		conXYZSDXTimePanel = new JPanel();
		conXYZSDX.add(conXYZSDXTimePanel);
		conXYZSDXTimePanel.setLayout(new GridLayout(0, 2, 0, 0));
		
		conXYZSDXTime = new JLabel("Timestep");
		conXYZSDXTime.setHorizontalAlignment(SwingConstants.CENTER);
		conXYZSDXTimePanel.add(conXYZSDXTime);
		
		conXYZSDXTimeSpin = new JSpinner();
		conXYZSDXTimeSpin.setModel(new SpinnerNumberModel(new Double(0.01), new Double(0), null, new Double(1)));
		conXYZSDXTimePanel.add(conXYZSDXTimeSpin);
		
		conXYZSDXConvButton = new JButton("Convert");
		conXYZSDXConvButton.addActionListener(new XYZSDXGoListener());
		conXYZSDX.add(conXYZSDXConvButton);
		
		runningPanel = new JPanel();
		viewPanel.add(runningPanel, CARDRUNNING);
		
		JProgressBar runningPanProg = new JProgressBar(0, 100);
		runningPanProg.setIndeterminate(true);
		runningPanel.add(runningPanProg);
		
		runningError = new JLabel("");
		runningPanel.add(runningError);
		
		taskSel = new JComboBox<String>();
		taskSel.setModel(new DefaultComboBoxModel<String>(new String[] {CARDVAC, CARDMSD, CARDCONVERT}));
		taskSel.addActionListener(new TaskSelectionListener(taskSel, viewPanelLayout, viewPanel));
		contentPane.add(taskSel, BorderLayout.NORTH);
		
		running = new JFrame("Task running");
		running.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		runningProg = new JProgressBar(0, 100);
		runningProg.setIndeterminate(true);
		running.add(runningProg, BorderLayout.CENTER);
		running.pack();
	}
	
	/**
	 * This responds to calls to browse for a file.
	 * @author Benjamin
	 */
	protected class BrowseFileListener implements ActionListener{
		/**The text field to put the selected file in.*/
		protected JTextField toSet;
		/**The file chooser to present to the user.*/
		protected JFileChooser toSearch;
		/**
		 * This sets up a responder to browse requests.
		 * @param toSet The text field to put the selected file in.
		 * @param toSearch The file chooser to present to the user.
		 */
		public BrowseFileListener(JTextField toSet, JFileChooser toSearch){
			this.toSet = toSet;
			this.toSearch = toSearch;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			int result = toSearch.showOpenDialog(GUI.this);
			if(result == JFileChooser.APPROVE_OPTION){
				toSet.setText(toSearch.getSelectedFile().getAbsolutePath());
			}
		}
	}
	
	/**
	 * This responds to changing what task is selected.
	 * @author Benjamin
	 */
	protected class TaskSelectionListener implements ActionListener{
		/**The combo box to get the task selection from.*/
		protected JComboBox<String> toGetSel;
		/**The layout to change what's showing with.*/
		protected CardLayout toShow;
		/**The panel to change what's showing in.*/
		protected JPanel container;
		/**
		 * This changes what's showing to match what the user selected.
		 * @param toGetSel The combo box to get the task selection from.
		 * @param toShow The layout to change what's showing with.
		 * @param container The panel to change what's showing in.
		 */
		public TaskSelectionListener(JComboBox<String> toGetSel, CardLayout toShow, JPanel container) {
			this.toGetSel = toGetSel;
			this.toShow = toShow;
			this.container = container;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			String selectedTask = (String) toGetSel.getSelectedItem();
			toShow.show(container, selectedTask);
		}
	}
	
	/**
	 * This actually runs the VAC program on request.
	 * @author Benjamin
	 */
	protected class VACGoListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			GUI.this.runningError.setText("");
			GUI.this.taskSel.setEnabled(false);
			GUI.this.viewPanelLayout.show(GUI.this.viewPanel, CARDRUNNING);
			Runnable toRun = new Runnable() {
				@Override
				public void run() {
					String selectedAlgorithm = (String) vacAlgSel.getSelectedItem();
					String source = vacSourceTxt.getText();
					String dest = vacDestTxt.getText();
					String working = vacWorkTxt.getText();
					String numResamps = vacNumResampsSpin.getValue().toString();
					String numDims = vacDimsSpin.getValue().toString();
					String aggNumber = vacAggSpin.getValue().toString();
					String timeClip = vacClipSpin.getValue().toString();
					String distDest = vacDistDestTxt.getText();
					String varCutoff = vacVarRatSpin.getValue().toString();
					boolean inRam = vacInRAM.isSelected();
					try{
						if(inRam){
							String selAlg = "full";
							switch(selectedAlgorithm){
								case ALGSTANDARD:
									selAlg = "full";
									break;
								case ALGONEPASS:
									selAlg = "onepass";
									break;
								case ALGMULTITAU:
									selAlg = "multitau";
									break;
								case ALGCLIPREPEAT:
									selAlg = "cliprepeat";
									break;
								default:
									//wtf
									break;
							}
							VACnRAM.main(new String[]{"-src="+source, "-dst="+dest, "-num="+numResamps, "-dim="+numDims, "-adc="+distDest, "-vcr="+varCutoff, "-alg="+selAlg, "-mta="+aggNumber, "-crl="+timeClip});
						}
						else{
							switch (selectedAlgorithm) {
								case ALGSTANDARD:
									VAC.main(new String[]{"-src="+source, "-wrk="+working, "-dst="+dest, "-num="+numResamps, "-dim="+numDims, "-adc="+distDest, "-vcr="+varCutoff});
									break;
								case ALGONEPASS:
									VACOnePass.main(new String[]{"-src="+source, "-wrk="+working, "-dst="+dest, "-num="+numResamps, "-dim="+numDims, "-adc="+distDest, "-vcr="+varCutoff});
									break;
								case ALGMULTITAU:
									VACMultiTau.main(new String[]{"-src="+source, "-wrk="+working, "-dst="+dest, "-num="+numResamps, "-dim="+numDims, "-agg="+aggNumber, "-adc="+distDest, "-vcr="+varCutoff});
									break;
								case ALGCLIPREPEAT:
									VACClipRepeat.main(new String[]{"-src="+source, "-wrk="+working, "-dst="+dest, "-num="+numResamps, "-dim="+numDims, "-clp="+timeClip, "-adc="+distDest, "-vcr="+varCutoff});
									break;
								default:
									//wtf
									break;
								}
						}
						GUI.this.viewPanelLayout.show(GUI.this.viewPanel, CARDVAC);
					}
					catch(Exception ex){
						GUI.this.runningError.setText(ex.getMessage());
					}
					GUI.this.taskSel.setEnabled(true);
				}
			};
			Thread runThread = new Thread(toRun);
			runThread.start();
		}
	}
	
	/**
	 * This actually runs the MSD program on request.
	 * @author Benjamin
	 */
	protected class MSDGoListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			GUI.this.runningError.setText("");
			GUI.this.taskSel.setEnabled(false);
			GUI.this.viewPanelLayout.show(GUI.this.viewPanel, CARDRUNNING);
			Runnable toRun = new Runnable() {
				@Override
				public void run() {
					String selectedAlgorithm = (String) msdAlgSel.getSelectedItem();
					String source = msdSourceTxt.getText();
					String dest = msdDestTxt.getText();
					String working = msdWorkTxt.getText();
					String numResamps = msdResampSpin.getValue().toString();
					String numDims = msdDimsSpin.getValue().toString();
					String aggNumber = msdAggSpin.getValue().toString();
					String timeClip = msdClipSpin.getValue().toString();
					String curve = msdCurveSpin.getValue().toString();
					String signal = msdSTNSpin.getValue().toString();
					String distDest = msdDistDestTxt.getText();
					boolean inRam = msdInRAM.isSelected();
					try{
						if(inRam){
							String selAlg = "full";
							switch(selectedAlgorithm){
								case ALGSTANDARD:
									selAlg = "full";
									break;
								case ALGONEPASS:
									selAlg = "onepass";
									break;
								case ALGMULTITAU:
									selAlg = "multitau";
									break;
								case ALGCLIPREPEAT:
									selAlg = "cliprepeat";
									break;
								default:
									//wtf
									break;
							}
							MSDnRAM.main(new String[]{"-src="+source, "-dst="+dest, "-num="+numResamps, "-cur="+curve, "-sig="+signal, "-dim="+numDims, "-adc="+distDest, "-alg="+selAlg, "-mta="+aggNumber, "-crl="+timeClip});
						}
						else{
							switch (selectedAlgorithm) {
							case ALGSTANDARD:
								MSD.main(new String[]{"-src="+source, "-wrk="+working, "-dst="+dest, "-num="+numResamps, "-cur="+curve, "-sig="+signal, "-dim="+numDims, "-adc="+distDest});
								break;
							case ALGONEPASS:
								MSDOnePass.main(new String[]{"-src="+source, "-wrk="+working, "-dst="+dest, "-num="+numResamps, "-cur="+curve, "-sig="+signal, "-dim="+numDims, "-adc="+distDest});
								break;
							case ALGMULTITAU:
								MSDMultiTau.main(new String[]{"-src="+source, "-wrk="+working, "-dst="+dest, "-num="+numResamps, "-cur="+curve, "-sig="+signal, "-dim="+numDims, "-agg="+aggNumber, "-adc="+distDest});
								break;
							case ALGCLIPREPEAT:
								MSDClipRepeat.main(new String[]{"-src="+source, "-wrk="+working, "-dst="+dest, "-num="+numResamps, "-cur="+curve, "-sig="+signal, "-dim="+numDims, "-clp="+timeClip, "-adc="+distDest});
								break;
							default:
								//wtf
								break;
							}
						}
						GUI.this.viewPanelLayout.show(GUI.this.viewPanel, CARDMSD);
					}
					catch(Exception ex){
						GUI.this.runningError.setText(ex.getMessage());
					}
					GUI.this.taskSel.setEnabled(true);
				}
			};
			Thread runThread = new Thread(toRun);
			runThread.start();
		}
	}
	
	/**
	 * This responds to calls to convert an SDX trajectory to an SDB.
	 * @author Benjamin
	 */
	protected class SDXSDBGoListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			GUI.this.runningError.setText("");
			GUI.this.taskSel.setEnabled(false);
			GUI.this.viewPanelLayout.show(GUI.this.viewPanel, CARDRUNNING);
			Runnable toRun = new Runnable() {
				@Override
				public void run() {
					String source = conSDXSDBSourceTxt.getText();
					String dest = conSDXSDBDestTxt.getText();
					try {
						ConvertSDXtoSDB.main(new String[]{"-src="+source, "-dst="+dest});
						GUI.this.viewPanelLayout.show(GUI.this.viewPanel, CARDCONVERT);
					} catch (Exception ex) {
						GUI.this.runningError.setText(ex.getMessage());
					}
					GUI.this.taskSel.setEnabled(true);
				}
			};
			Thread runThread = new Thread(toRun);
			runThread.start();
		}
	}
	
	/**
	 * This responds to calls to convert an SDX trajectory to an SDB.
	 * @author Benjamin
	 */
	protected class SDBSDXGoListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			GUI.this.runningError.setText("");
			GUI.this.taskSel.setEnabled(false);
			GUI.this.viewPanelLayout.show(GUI.this.viewPanel, CARDRUNNING);
			Runnable toRun = new Runnable() {
				@Override
				public void run() {
					String source = conSDBSDXSourceTxt.getText();
					String dest = conSDBSDXDestTxt.getText();
					try {
						ConvertSDBtoSDX.main(new String[]{"-src="+source, "-dst="+dest});
						GUI.this.viewPanelLayout.show(GUI.this.viewPanel, CARDCONVERT);
					} catch (Exception ex) {
						GUI.this.runningError.setText(ex.getMessage());
					}
					GUI.this.taskSel.setEnabled(true);
				}
			};
			Thread runThread = new Thread(toRun);
			runThread.start();
		}
	}
	
	/**
	 * This responds to calls to convert lammps dumps to sdx.
	 * @author Benjamin
	 */
	protected class LammpsSDXGoListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			GUI.this.runningError.setText("");
			GUI.this.taskSel.setEnabled(false);
			GUI.this.viewPanelLayout.show(GUI.this.viewPanel, CARDRUNNING);
			Runnable toRun = new Runnable() {
				@Override
				public void run() {
					String source = conLammpsSDXSourceTxt.getText();
					String dest = conLammpsSDXDestTxt.getText();
					String dims = conLammpsSDXDimSpin.getValue().toString();
					String timestep = conLammpsSDXTimeSpin.getValue().toString();
					try {
						LAMMPSDumpToSDX.main(new String[]{"-src="+source, "-dst="+dest, "-dim="+dims, "-tim="+timestep});
						GUI.this.viewPanelLayout.show(GUI.this.viewPanel, CARDCONVERT);
					} catch (Exception ex) {
						GUI.this.runningError.setText(ex.getMessage());
					}
					GUI.this.taskSel.setEnabled(true);
				}
			};
			Thread runThread = new Thread(toRun);
			runThread.start();
		}
	}
	
	/**
	 * This responds to calls to convert xyz to sdx.
	 * @author Benjamin
	 */
	protected class XYZSDXGoListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			GUI.this.runningError.setText("");
			GUI.this.taskSel.setEnabled(false);
			GUI.this.viewPanelLayout.show(GUI.this.viewPanel, CARDRUNNING);
			Runnable toRun = new Runnable() {
				@Override
				public void run() {
					String source = conXYZSDXSourceTxt.getText();
					String dest = conXYZSDXDestTxt.getText();
					String dims = conXYZSDXDimSpin.getValue().toString();
					String timestep = conXYZSDXTimeSpin.getValue().toString();
					try {
						XYZToSDX.main(new String[]{"-src="+source, "-dst="+dest, "-dim="+dims, "-tim="+timestep});
						GUI.this.viewPanelLayout.show(GUI.this.viewPanel, CARDCONVERT);
					} catch (Exception ex) {
						GUI.this.runningError.setText(ex.getMessage());
					}
					GUI.this.taskSel.setEnabled(true);
				}
			};
			Thread runThread = new Thread(toRun);
			runThread.start();
		}
	}
	
	/**
	 * This responds to selections of algorithm.
	 * @author Benjamin
	 */
	protected class AlgorithmSelectionListener implements ActionListener{
		/**The component to enable when multitau is selected.*/
		protected JComponent multitauComp;
		/**The component to enable when clip repeat is selected.*/
		protected JComponent clipRepComp;
		/**The box used to select the algorithm.*/
		protected JComboBox<String> algSel;
		/**
		 * This sets up a listener.
		 * @param algSel The box used to select the algorithm.
		 * @param clipRepComp The component to enable when clip repeat is selected.
		 * @param multitauComp The component to enable when multitau is selected.
		 */
		public AlgorithmSelectionListener(JComboBox<String> algSel, JComponent clipRepComp, JComponent multitauComp) {
			this.algSel = algSel;
			this.clipRepComp = clipRepComp;
			this.multitauComp = multitauComp;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			String selAlg = (String) algSel.getSelectedItem();
			switch (selAlg) {
			case ALGCLIPREPEAT:
				clipRepComp.setEnabled(true);
				multitauComp.setEnabled(false);
				break;
			case ALGMULTITAU:
				clipRepComp.setEnabled(false);
				multitauComp.setEnabled(true);
				break;
			default:
				clipRepComp.setEnabled(false);
				multitauComp.setEnabled(false);
				break;
			}
		}
	}
}

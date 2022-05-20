package com.connect.azure;

import com.azure.core.management.profile.AzureProfile;

import javax.swing.*;

import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import com.microsoft.azure.AzureEnvironment;
import com.microsoft.azure.credentials.ApplicationTokenCredentials;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.monitor.*;
import com.microsoft.azure.management.monitor.MetadataValue;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.xy.XYDataset;
import org.joda.time.DateTime;
import org.joda.time.Period;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;Eg
import java.util.*;
import java.util.List;

import com.microsoft.azure.management.monitor.MetricCollection;

public class DesCreateTest {

        static List<Double> a1 = new ArrayList<>();
        static List<DateTime> a2 = new ArrayList<>();
        static String tit;
        static int hour;
        static int frame;
public static class LineChartExample extends JFrame {

        private static final long serialVersionUID = 1L;

        public LineChartExample(String title) {
            super(title);
            // Create dataset
            DefaultCategoryDataset dataset = createDataset();
            // Create chart
            JFreeChart chart = ChartFactory.createLineChart(
                    tit, // Chart title
                    "Date and Time", // X-Axis Label
                    "Average Value", // Y-Axis Label
                    dataset
            );
            ChartPanel chartPanel = new ChartPanel(chart) ;

            chartPanel.addChartMouseListener(new ChartMouseListener() {

                @Override
                public void chartMouseClicked(ChartMouseEvent e) {
                    final ChartEntity entity = e.getEntity();
                    System.out.println(entity + " " + entity.getArea());
                }

                @Override
                public void chartMouseMoved(ChartMouseEvent e) {
                }
            });
            chart.setBackgroundPaint(Color.white);
            ChartUtilities.applyCurrentTheme(chart);
            final CategoryPlot plot = (CategoryPlot) chart.getPlot();
            plot.setBackgroundPaint(Color.black);
            plot.setRangeGridlinePaint(Color.white);
            CategoryAxis catAxis = plot.getDomainAxis();
            CategoryItemRenderer rendu = plot.getRenderer();
            rendu.setSeriesPaint(0, new Color(255,255,100));
            catAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);
            catAxis.setTickLabelPaint(new Color(0,0,0,0));
            int y1=0;
            if(frame == 1)
            {
                y1 = hour;
            }
            else if(frame == 5)
            {
                y1=5*hour/24;
            }
            else if(frame == 15)
            {
                y1 = 5*hour/24;
                y1=y1/3;
            }
            else if(frame == 30)
            {
                y1 = 5*hour/24;
                y1=y1/6;
            }
            else if(frame == 60)
            {
                y1 = 5*hour/24;
                y1=y1/6;
            }
            for(int i=0;i<a2.size();i=i+y1)
            {
                //String cat_Name = (String) plot.getCategories().get(i-1);
                catAxis.setTickLabelPaint(a2.get(i), Color.black);
            }
            Font nwfont=new Font("Arial",0,10);
            NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setTickLabelFont(nwfont);
            catAxis.setTickLabelFont(nwfont);
            File imageFile = new File("LineChart.png");
            int width = 1200;
            int height = 700;
            plot.setOutlinePaint(Color.GRAY);
            plot.setOutlineStroke(new BasicStroke(2.0f));
            try {
                ChartUtilities.saveChartAsPNG(imageFile, chart, width, height);
            } catch (IOException ex) {
                System.err.println(ex);
            }
            ChartPanel panel = new ChartPanel(chart);
            setContentPane(panel);
        }
    }

        private static DefaultCategoryDataset createDataset() {
            String series1 = tit;
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for(int i=0;i<a1.size();i++)
            {
                dataset.addValue(a1.get(i),series1, a2.get(i));
            }
            return dataset;
    }


        public static void testCall() {
            String tenant = "f636e1c4-18a5-4f8a-9e41-e247bae1387d";
            String clientId = "5f52ade6-0fd1-4b73-b0c7-4a22e8e9daf2";
                //String Id = "/subscriptions/3ddda1c7-d1f5-4e7b-ac81-0523f483b3b3/resourceGroups/juhig/providers/Microsoft.Storage/storageAccounts/juhig";
            String passwd = "OOS~.VExdru9djm5CV4F~z7u1rEI0CvphJ";
            String sub = "3ddda1c7-d1f5-4e7b-ac81-0523f483b3b3";


     /*   Scanner sc1 = new Scanner(System.in);
        System.out.print("Enter the Client Id: ");
        String clientId = sc1.nextLine();
        System.out.print("Enter the Password: ");
        Scanner sc2 = new Scanner(System.in);
        String passwd = sc2.nextLine();
        System.out.print("Enter Subscription Id: ");
        Scanner sc4 = new Scanner(System.in);
        String sub = sc4.nextLine();*/

            System.out.print("Enter Resource name: ");
            Scanner sc5 = new Scanner(System.in);
            String Id1 = sc5.nextLine();
            String Id= "/subscriptions/3ddda1c7-d1f5-4e7b-ac81-0523f483b3b3/resourceGroups/" + Id1 + "/providers/Microsoft.Storage/storageAccounts/" +Id1;


            AzureProfile profile = new AzureProfile(tenant, sub, com.azure.core.management.AzureEnvironment.AZURE);//AzureProfile(tenant,sub,AzureEnvironment.AZURE);
            ApplicationTokenCredentials crede = new ApplicationTokenCredentials(clientId, tenant, passwd, AzureEnvironment.AZURE);
            Azure azure = Azure.authenticate(crede).withSubscription("3ddda1c7-d1f5-4e7b-ac81-0523f483b3b3");

            DateTime record = DateTime.now();
            System.out.println("Enter a Metrics Name from Availability, Egress, Ingress, Success E2E Latency, Success Server Latency, Transactions");
            Scanner s = new Scanner(System.in);
            String metr = s.nextLine();
            tit = metr;
            System.out.println("Number of days for which Data is Required:");
            Scanner s1 = new Scanner(System.in);
            var days = s1.nextInt();
            days = days*24;
            System.out.println("Enter the Epoch Value in 1/5/15/30/60 min:");
            Scanner s2 = new Scanner(System.in);
            int eco = s2.nextInt();
            hour = days;
            frame = eco;
            System.out.println("Enter the Aggregation (Average, Minimum, Maximum, Total, Count):");
            Scanner s3 = new Scanner(System.in);
            String arg = s3.nextLine();
            for (MetricDefinition metricDefinition : azure.metricDefinitions().listByResource(Id)) {
                // find metric definition for Transactions
                //System.out.println("Connection Done");
                if (metricDefinition.name().localizedValue().equalsIgnoreCase(metr)) {
                    // get metric records
                    MetricCollection metricCollection = metricDefinition.defineQuery()
                            .startingFrom(record.minusHours(days))
                            .endsBefore(record)
                            .withAggregation(arg)
                            .withInterval(Period.minutes(eco))
                            //.withOdataFilter("responseType ne 'Success'")
                            //.withOdataFilter("apiName eq 'PutBlob' and responseType eq 'Success' and geoType eq 'Primary'")
                            .execute();
                    System.out.println("Metrics for '" + Id + "':");
                    System.out.println("Namespacse: " + metricCollection.namespace());
                    System.out.println("Query time: " + metricCollection.timespan());
                    System.out.println("Time Grain: " + metricCollection.interval());
                    System.out.println("Cost: " + metricCollection.cost());

                    for (Metric metric : metricCollection.metrics()) {
                        System.out.println("\tMetric: " + metric.name().localizedValue());
                        System.out.println("\tType: " + metric.type());
                        System.out.println("\tUnit: " + metric.unit());
                        System.out.println("\tTime Series: ");
                        for (TimeSeriesElement timeElement : metric.timeseries()) {
                            System.out.println("\t\tMetadata: ");
                            for (MetadataValue metadata : timeElement.metadatavalues()) {
                                System.out.println("\t\t\t" + metadata.name().localizedValue() + ": " + metadata.value());
                            }
                            System.out.println("\t\tData: ");
                            for (MetricValue data : timeElement.data()) {

                                if(arg.equals("Average"))
                                {
                                    a1.add(data.average());
                                }
                                else if (arg.equals("Total"))
                                {
                                    a1.add(data.total());
                                }
                                else if (arg.equals("Maximum"))
                                {
                                    a1.add(data.maximum());
                                }
                                else if (arg.equals("Minimum"))
                                {
                                    a1.add(data.minimum());
                                }
                                else if (arg.equals("Count"))
                                {
                                    a1.add(data.count());
                                }
                                a2.add(data.timeStamp());
                                System.out.println(
                                        " : (Time) " + data.timeStamp()
                                                + " : (Min) " + data.minimum()
                                                + " : (Max) " + data.maximum()
                                                + " : (Avg) " + data.average()
                                                + " : (Total) " + data.total()
                                                + " : (Count) " + data.count());

                            }
                        }
                    }
                    break;
                }
            }

        }

        public static void main(String[] args) {
            //BasicConfigurator.configure();

            testCall();
            SwingUtilities.invokeLater(() -> {
                LineChartExample example = new LineChartExample("Metrics");
                example.setAlwaysOnTop(true);
                example.pack();
                example.setSize(1200, 700);
                example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                example.setVisible(true);
                int domainAxis = example.getX();

            });
        }
}



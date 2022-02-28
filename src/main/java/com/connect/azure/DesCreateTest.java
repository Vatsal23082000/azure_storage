package com.connect.azure;

import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.eventhubs.models.EventHubNamespace;
import com.azure.resourcemanager.monitor.fluent.models.MetadataValueInner;
import com.azure.resourcemanager.monitor.models.EventData;
import com.azure.resourcemanager.storage.models.StorageAccount;
import com.microsoft.azure.AzureEnvironment;
import com.microsoft.azure.credentials.ApplicationTokenCredentials;
import com.microsoft.azure.management.Azure;
import com.azure.storage.blob.*;
import com.azure.storage.blob.models.*;
import com.microsoft.azure.management.monitor.*;
import com.microsoft.azure.management.monitor.MetadataValue;
import org.joda.time.DateTime;
import org.joda.time.Period;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Map;

public class DesCreateTest {
    public static void testCall() {

        String clientId = "5328f62e-e776-408a-a8b3-50279eb53875";
        String tenant = "f636e1c4-18a5-4f8a-9e41-e247bae1387d";
        String passwd = "Drkw17VvreqW~hyxus.TJdboCeG9FeozHI";
        ApplicationTokenCredentials cred = new ApplicationTokenCredentials(clientId, tenant, passwd, AzureEnvironment.AZURE);
        Azure azure = Azure.authenticate(cred).withSubscription("3ddda1c7-d1f5-4e7b-ac81-0523f483b3b3");
        String account_name="juhig";
        String account_access_key="+TT48kUyXxa7BAH5gyxPbjlMb0ffplKecokit4PsMb/Q3p58pckE6AXDoEAjeoiYR8zWNh7wR/Gh4v3tykPf0A==";
        String Id = "/subscriptions/3ddda1c7-d1f5-4e7b-ac81-0523f483b3b3/resourceGroups/juhig/providers/Microsoft.Storage/storageAccounts/juhig";
        System.out.println("Connection done");


        //OffsetDateTime recordDateTime = OffsetDateTime.now();
        DateTime record = DateTime.now();
        // get metric definitions for storage account.
        for (MetricDefinition metricDefinition : azure.metricDefinitions().listByResource(Id)) {
            // find metric definition for Transactions
            if (metricDefinition.name().localizedValue().equalsIgnoreCase("transactions")) {
                // get metric records
                MetricCollection metricCollection = metricDefinition.defineQuery()
                        .startingFrom(record.minusDays(7))
                        .endsBefore(record)
                        .withAggregation("Average")
                        .withInterval(Period.minutes(5))
                        .withOdataFilter("apiName eq 'PutBlob' and responseType eq 'Success' and geoType eq 'Primary'")
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
                            System.out.println(
                                    " : (Min) " + data.minimum()
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
    public static void main(String[] args){
        testCall();
    }
}

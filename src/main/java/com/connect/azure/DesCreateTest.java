package com.connect.azure;

import com.azure.core.http.rest.PagedIterable;
import com.azure.core.management.profile.AzureProfile;
import com.azure.resourcemanager.AzureResourceManager;
import org.apache.log4j.BasicConfigurator;
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
import rx.Observable;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.identity.ClientSecretCredential;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import com.microsoft.azure.management.monitor.MetricCollection;
import com.azure.resourcemanager.monitor.models.MetricDefinition;
import com.microsoft.azure.AzureEnvironment;
public class DesCreateTest {

    public static void testCall() {

        String clientId = "5328f62e-e776-408a-a8b3-50279eb53875";
        String tenant = "f636e1c4-18a5-4f8a-9e41-e247bae1387d";
        String passwd = "Drkw17VvreqW~hyxus.TJdboCeG9FeozHI";
        String sub ="3ddda1c7-d1f5-4e7b-ac81-0523f483b3b3";


        ClientSecretCredential cred = new ClientSecretCredentialBuilder()
                .clientId(clientId)
                .clientSecret(passwd)
                .tenantId(tenant)
                .build();

       // AzureProfile profile = new AzureProfile(com.azure.core.management.AzureEnvironment.AZURE);
        AzureProfile profile = new AzureProfile(tenant,sub,com.azure.core.management.AzureEnvironment.AZURE) ;//AzureProfile(tenant,sub,AzureEnvironment.AZURE);

        //ApplicationTokenCredentials crede = new ApplicationTokenCredentials(clientId, tenant, passwd, AzureEnvironment.AZURE);
        //Azure azure = Azure.authenticate(crede).withSubscription("3ddda1c7-d1f5-4e7b-ac81-0523f483b3b3");

        AzureResourceManager azure = AzureResourceManager.authenticate(cred, profile).withDefaultSubscription();
        //AzureResourceManager azure = AzureResourceManager.authenticate(cred).withSubscription("3ddda1c7-d1f5-4e7b-ac81-0523f483b3b3");
        String account_name="juhig";
        String account_access_key="+TT48kUyXxa7BAH5gyxPbjlMb0ffplKecokit4PsMb/Q3p58pckE6AXDoEAjeoiYR8zWNh7wR/Gh4v3tykPf0A==";
        String Id = "/subscriptions/3ddda1c7-d1f5-4e7b-ac81-0523f483b3b3/resourceGroups/juhig/providers/Microsoft.Storage/storageAccounts/juhig";




        OffsetDateTime recordDateTime = OffsetDateTime.now();
        DateTime record = DateTime.now();

        //System.out.println("Connection done");
        // get metric definitions for storage account.
        PagedIterable<MetricDefinition> met = azure.metricDefinitions().listByResource(Id);
        met.streamByPage().forEach(resp -> {
            System.out.printf("Response headers are %s. Url %s  and status code %d %n", resp.getHeaders(),
                    resp.getRequest().getUrl(), resp.getStatusCode());
            resp.getElements().forEach(value -> System.out.printf("Response value is %d %n", value));
        });
        //System.out.println(met);

        for (MetricDefinition metricDefinition : azure.metricDefinitions().listByResource(Id)) {
            // find metric definition for Transactions
            System.out.println("Connection Done");
            if (metricDefinition.name().localizedValue().equalsIgnoreCase("transactions")) {
                // get metric records
                MetricCollection metricCollection = (MetricCollection) metricDefinition.defineQuery()
                        .startingFrom(recordDateTime.minusDays(7))
                        .endsBefore(recordDateTime)
                        .withAggregation("Average")
                        .withInterval(Duration.ofMinutes(5))
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
        //BasicConfigurator.configure();
        testCall();
    }
}

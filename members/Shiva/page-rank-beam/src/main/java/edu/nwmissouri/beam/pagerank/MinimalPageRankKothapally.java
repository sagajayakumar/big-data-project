package edu.nwmissouri.beam.pagerank;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

import com.fasterxml.jackson.databind.type.CollectionLikeType;

// beam-playground:
//   name: MinimalWordCount
//   description: An example that counts words in Shakespeare's works.
//   multifile: false
//   pipeline_options:
//   categories:
//     - Combiners
//     - Filtering
//     - IO
//     - Core Transforms

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.Filter;
import org.apache.beam.sdk.transforms.Flatten;
import org.apache.beam.sdk.transforms.GroupByKey;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionList;
import org.apache.beam.sdk.values.TypeDescriptors;

public class MinimalPageRankKothapally {

  public static void main(String[] args) {
    PipelineOptions options = PipelineOptionsFactory.create();
    Pipeline p = Pipeline.create(options);
    String dataFolder = "web04";

    PCollection<KV<String, String>> PCollectionskvLinksKV1 = KothapallyMapJob(p, "go.md", dataFolder);
    PCollection<KV<String, String>> PCollectionskvLinksKV2 = KothapallyMapJob(p, "java.md", dataFolder);
    PCollection<KV<String, String>> PCollectionskvLinksKV3 = KothapallyMapJob(p, "python.md", dataFolder);
    PCollection<KV<String, String>> PCollectionskvLinksKV4 = KothapallyMapJob(p, "ReadMe.md", dataFolder);

    PCollectionList<KV<String, String>> result = PCollectionList.of(PCollectionskvLinksKV1).and(PCollectionskvLinksKV2)
        .and(PCollectionskvLinksKV3).and(PCollectionskvLinksKV4);

    PCollection<KV<String, String>> mergedList = result.apply(Flatten.<KV<String, String>>pCollections());

    PCollection<KV<String, Iterable<String>>> group = mergedList.apply(GroupByKey.create());
    PCollection<String> pLinksStr = group
        .apply(MapElements.into(TypeDescriptors.strings()).via((mergeOut) -> mergeOut.toString()));

    pLinksStr.apply(TextIO.write().to("Kothapallycounts"));
    p.run().waitUntilFinish();
  }

  private static PCollection<KV<String, String>> KothapallyMapJob(Pipeline p, String dataFile, String Path) {
    String dp = Path + "/" + dataFile;
    PCollection<String> PCILines = p.apply(TextIO.read().from(dp));
    PCollection<String> PCLines = PCILines.apply(Filter.by((String line) -> !line.isEmpty()));
    PCollection<String> PCIEmptyLines = PCLines.apply(Filter.by((String line) -> !line.equals(" ")));
    PCollection<String> PCILLines = PCIEmptyLines.apply(Filter.by((String line) -> line.startsWith("[")));

    PCollection<String> PCLinks = PCILLines.apply(
        MapElements.into(TypeDescriptors.strings())
            .via((String linelink) -> linelink.substring(linelink.indexOf("(") + 1, linelink.indexOf(")"))));

    PCollection<KV<String, String>> kv = PCLinks.apply(
        MapElements.into(
            TypeDescriptors.kvs(TypeDescriptors.strings(), TypeDescriptors.strings()))
            .via(linelink -> KV.of(dataFile, linelink)));
    return kv;
  }

  static class Job1Finalizer extends DoFn<KV<String, Iterable<String>>, KV<String, RankedPage>> {
    @ProcessElement
    public void processElement(@Element KV<String, Iterable<String>> element,
        OutputReceiver<KV<String, RankedPage>> receiver) {
      Integer contributorVotes = 0;
      if (element.getValue() instanceof Collection) {
        contributorVotes = ((Collection<String>) element.getValue()).size();
      }
      ArrayList<VotingPage> voters = new ArrayList<VotingPage>();
      for (String voterName : element.getValue()) {
        if (!voterName.isEmpty()) {
          voters.add(new VotingPage(voterName, contributorVotes));
        }
      }
      receiver.output(KV.of(element.getKey(), new RankedPage(element.getKey(), voters)));
    }
  }

  static class Job2Mapper extends DoFn<KV<String, RankedPage>, KV<String, RankedPage>> {

    @ProcessElement
    public void processElement(@Element KV<String, RankedPage> element,
        OutputReceiver<KV<String, RankedPage>> receiver) {
      Integer votes = 0;
      ArrayList<VotingPage> voters = element.getValue().getVoted();
      if (voters instanceof Collection) {
        votes = ((Collection<VotingPage>) voters).size();
      }

      for (VotingPage votingPage : voters) {
        String pageName = votingPage.getVoterName();
        Double pageRank = votingPage.getVoterRank();
        String contributingPageName = element.getKey();
        Double contributingPageRank = element.getValue().getRank();
        VotingPage contributor = new VotingPage(contributingPageName, contributingPageRank, votes);
        ArrayList<VotingPage> arr = new ArrayList<>();
        arr.add(contributor);
        receiver.output(KV.of(votingPage.getVoterName(), new RankedPage(pageName, pageRank, arr)));
      }
    }

  }

  static class Job2Updater extends DoFn<KV<String, Iterable<RankedPage>>, KV<String, RankedPage>> {

    @ProcessElement
    public void processElement(@Element KV<String, Iterable<RankedPage>> element,
        OutputReceiver<KV<String, RankedPage>> receiver) {
      String thisPage = element.getKey();
      Iterable<RankedPage> rankedPages = element.getValue();
      Double dampingFactor = 0.85;
      Double updatedRank = (1 - dampingFactor);
      ArrayList<VotingPage> newVoters = new ArrayList<VotingPage>();

      for (RankedPage rankedPage : rankedPages) {
        if (rankedPage != null) {
          for (VotingPage votePage : rankedPage.getVoted()) {
            newVoters.add(votePage);
            updatedRank += (dampingFactor) * votePage.getVoterRank() / (double) votePage.getVoterRank();
          }
        }
      }

      receiver.output(KV.of(thisPage, new RankedPage(thisPage, updatedRank, newVoters)));
    }
  }

  static class Job3Finder extends DoFn<KV<String, RankedPage>, KV<String, Double>> {
    @ProcessElement
    public void processElement(@Element KV<String, RankedPage> element,
        OutputReceiver<KV<String, Double>> receiver) {
      String currentPage = element.getKey();
      Double currentPageRank = element.getValue().getRank();

      receiver.output(KV.of(currentPage, currentPageRank));
    }
  }

  public static class Job3Final implements Comparator<KV<String, Double>>, Serializable {
    @Override
    public int compare(KV<String, Double> o1, KV<String, Double> o2) {
      return o1.getValue().compareTo(o2.getValue());
    }
  }

}
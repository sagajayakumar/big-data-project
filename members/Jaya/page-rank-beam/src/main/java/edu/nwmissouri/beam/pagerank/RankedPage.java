
package edu.nwmissouri.beam.pagerank;

import java.io.Serializable;
import java.util.ArrayList;

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

public class RankedPage implements Serializable {
        String name;
        double rank;
        int count;

        public RankedPage(String name, double rank, int count) {
                this.name = name;
                this.rank = rank;
                this.count = count;
        }

        public RankedPage(String name, Double rank) {
                this.name = name;
                this.rank = rank;
        }

        public RankedPage(String name, int rank) {
                this.name = name;
                this.rank = rank;
        }

        public RankedPage(String key, ArrayList<VotingPage> voters) {
        }

}

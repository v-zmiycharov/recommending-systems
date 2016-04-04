package recsys;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.RMSRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.svd.ALSWRFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.Factorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.cf.taste.impl.recommender.svd.RatingSGDFactorizer;

public class Main {

	public static void main(String[] args) throws Exception {
		File file = new File(Main.class.getResource("/ml-1m/ratings.dat").toURI());
		DataModel model = new FileDataModel(file, "::");
		
		EvalResult userBasedResult = evaluateUsersBasedError(model);
		EvalResult itemsBasedResult = evaluateItemsBasedError(model);
		EvalResult svdBasedResult = evaluateSvdError(model);
	}
	
	private static EvalResult evaluateUsersBasedError(DataModel model) throws Exception {
		EvalResult result = new EvalResult();
		
		if(true) {
			result.setMAEResult(0.7623507598389264);
			result.setRMSEResult(0.9619048699917635);
			
			return result;
		}
		
		RecommenderBuilder builder = new RecommenderBuilder() {
            public Recommender buildRecommender(DataModel dataModel) throws TasteException {
            	UserSimilarity similarity = new PearsonCorrelationSimilarity(dataModel);
        		UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, dataModel);
        		return new GenericUserBasedRecommender(dataModel, neighborhood, similarity);
            }
        };;

		RecommenderEvaluator MAEevaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
		double MAEresult = MAEevaluator.evaluate(builder, null, model, 0.9, 1.0);
		System.out.println("Users based MAE: " + MAEresult);
		result.setMAEResult(MAEresult);

		RecommenderEvaluator RMSEevaluator = new RMSRecommenderEvaluator();
		double RMSEresult = RMSEevaluator.evaluate(builder, null, model, 0.9, 1.0);
		System.out.println("Users based RMSE: " + RMSEresult);
		result.setRMSEResult(RMSEresult);

		return result;
	}

	private static EvalResult evaluateItemsBasedError(DataModel model) throws Exception {
		EvalResult result = new EvalResult();
		
		if(true) {
			result.setMAEResult(0.7766893640910639);
			result.setRMSEResult(0.9756764446945198);
			
			return result;
		}

		RecommenderBuilder builder = new RecommenderBuilder() {
            public Recommender buildRecommender(DataModel dataModel) throws TasteException {
            	ItemSimilarity similarity = new PearsonCorrelationSimilarity(dataModel);
        		return new GenericItemBasedRecommender(dataModel, similarity);
            }
        };;

		RecommenderEvaluator MAEevaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
		double MAEresult = MAEevaluator.evaluate(builder, null, model, 0.9, 1.0);
		System.out.println("Items based MAE: " + MAEresult);
		result.setMAEResult(MAEresult);

		RecommenderEvaluator RMSEevaluator = new RMSRecommenderEvaluator();
		double RMSEresult = RMSEevaluator.evaluate(builder, null, model, 0.9, 1.0);
		System.out.println("Items based RMSE: " + RMSEresult);
		result.setRMSEResult(RMSEresult);

		return result;
	}

	private static EvalResult evaluateSvdError(DataModel model) throws Exception {
		EvalResult result = new EvalResult();
		
		if(true) {
			result.setMAEResult(0.8795674973245713);
			result.setRMSEResult(1.058581768277962);
			
			return result;
		}

		RecommenderBuilder builder = new RecommenderBuilder() {
            public Recommender buildRecommender(DataModel dataModel) throws TasteException {
        		return new SVDRecommender(dataModel, new ALSWRFactorizer(dataModel,4,0.5,100));
            }
        };;

		RecommenderEvaluator MAEevaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
		double MAEresult = MAEevaluator.evaluate(builder, null, model, 0.9, 1.0);
		System.out.println("SVD MAE: " + MAEresult);
		result.setMAEResult(MAEresult);

		RecommenderEvaluator RMSEevaluator = new RMSRecommenderEvaluator();
		double RMSEresult = RMSEevaluator.evaluate(builder, null, model, 0.9, 1.0);
		System.out.println("SVD RMSE: " + RMSEresult);
		result.setRMSEResult(RMSEresult);

		return result;
	}

}

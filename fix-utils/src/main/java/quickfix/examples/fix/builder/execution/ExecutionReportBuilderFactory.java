package quickfix.examples.fix.builder.execution;

import java.util.HashMap;
import java.util.Map;

import quickfix.FixVersions;

public class ExecutionReportBuilderFactory {
	private Map<String, ExecutionReportBuilder> builders = new HashMap<String, ExecutionReportBuilder>();

	public ExecutionReportBuilderFactory() {
		builders.put(FixVersions.BEGINSTRING_FIX40, new FIX40ExecutionReportBuilder());
		builders.put(FixVersions.BEGINSTRING_FIX41, new FIX41ExecutionReportBuilder());
		builders.put(FixVersions.BEGINSTRING_FIX42, new FIX42ExecutionReportBuilder());
		builders.put(FixVersions.BEGINSTRING_FIX43, new FIX43ExecutionReportBuilder());
		builders.put(FixVersions.BEGINSTRING_FIX44, new FIX44ExecutionReportBuilder());
		builders.put(FixVersions.BEGINSTRING_FIXT11, new FIX50ExecutionReportBuilder());
	}

	public ExecutionReportBuilder getExecutionReportBuilder(String beginString) {
		return builders.get(beginString);
	}
}

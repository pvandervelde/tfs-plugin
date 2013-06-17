package hudson.plugins.tfs.commands;

import java.util.Calendar;
import java.util.List;

import hudson.plugins.tfs.model.ChangeSet;
import hudson.plugins.tfs.util.DateUtil;
import hudson.plugins.tfs.util.MaskedArgumentListBuilder;

public abstract class AbstractHistoryCommand extends AbstractCommand implements
        ParseableCommand<List<ChangeSet>> {

    protected final String projectPath;
    protected final Calendar fromTimestamp;
    protected final Calendar toTimestamp;

    protected AbstractHistoryCommand(ServerConfigurationProvider configurationProvider, String projectPath, Calendar fromTimestamp, Calendar toTimestamp) {
        super(configurationProvider);
        this.projectPath = projectPath;
        this.fromTimestamp = fromTimestamp;

        // The to timestamp is exclusive, ie it will only show history before the to timestamp.
        // This command should be inclusive.
        this.toTimestamp = (Calendar) toTimestamp.clone();
        this.toTimestamp.add(Calendar.SECOND, 1);
    }
    
    public MaskedArgumentListBuilder getArguments() {
        MaskedArgumentListBuilder arguments = new MaskedArgumentListBuilder();        
        arguments.add("history");
        arguments.add(projectPath);
        arguments.add("-noprompt");
        arguments.add(String.format("-version:D%s~D%s", 
                DateUtil.TFS_DATETIME_FORMATTER.get().format(fromTimestamp.getTime()), 
                DateUtil.TFS_DATETIME_FORMATTER.get().format(toTimestamp.getTime())));
        arguments.add("-recursive");
        arguments.add(String.format("-format:%s", getFormat()));        
        addServerArgument(arguments);
        addLoginArgument(arguments);
        return arguments;
    }

    protected abstract String getFormat();
   
}

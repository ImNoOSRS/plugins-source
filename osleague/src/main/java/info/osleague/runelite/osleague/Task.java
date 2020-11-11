package info.osleague.runelite.osleague;

import lombok.Data;

@Data
public class Task
{
	Task(int idx, int osLeagueIndex, String label, int points, boolean completed, int spriteId)
	{
		Index = idx;
		OsLeagueIndex = osLeagueIndex;
		Points = points;
		Label = label;
		Completed = completed;
		taskDifficulty = TaskDifficulty.fromSprite(spriteId);
	}

	public int Index;
	public int OsLeagueIndex;
	public boolean Completed;
	public String Label;
	public int Points;
	public TaskDifficulty taskDifficulty;
}

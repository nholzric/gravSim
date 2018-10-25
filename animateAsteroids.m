dataFilePath = '.\simLog.txt';

vecMag = @(v) sqrt(v(:,1).^2 + v(:,2).^2);
% vecDiff = @(a,b) sqrt((a(:,1)-b(:,1)).^2 + (a(:,2)-b(:,2)).^2);

if ~exist('orbitData','var')
    fprintf(1,'Load %s ',dataFilePath); tic;
    orbitData = readSimLog(dataFilePath);
    fprintf(1,'(%.1f)\n',toc);
else
    fprintf(1,'Skipping Loading Data\n');
end

uniqueTimes = unique(orbitData.time);
uniqueObjects = unique(orbitData.id);
numEntriesPerTime = size(uniqueObjects,1); %assume file is sorted by time
thisTimeIndex = 1:numEntriesPerTime;
minMaxAMag = [min(orbitData.aMag);max(orbitData.aMag)];
% figure(1);
% for i = 1:size(uniqueTimes,1)
%     xPlot = [orbitData.p(thisTimeIndex,1);];
%     yPlot = [orbitData.p(thisTimeIndex,2);];
%     cPlot = [orbitData.aMag(thisTimeIndex);];
%     scatter(xPlot,yPlot,[],cPlot);
% %     myYLim = ylim();
% %     myXLim = xlim();
% %     if(myYLim(2)-myYLim(1) > myXLim(2)- myXLim(1))
% %         xCenter = (myXLim(2)+myXLim(1))/2;
% %         extent = myYLim(2) - myYLim(1);
% %         xlim(xCenter + 0.5*[-extent,extent]);
% %     else
% %         yCenter = (myYLim(2)+myYLim(1))/2;
% %         extent = myXLim(2) - myXLim(1);
% %         ylim(yCenter + 0.5*[-extent,extent]);
% %     end
%     ylim(8E5*[-1,1]); xlim(8E5*[-1,1]);
%     pause(0.01);
%     thisTimeIndex = thisTimeIndex + numEntriesPerTime;
%     
% 
% end

% if ~exist('passDistance','var')
%     fprintf(1,'Calculating Pass Distances'); tic;
%     passDistance = nan(size(uniqueTimes,1),size(uniqueObjects,1),size(uniqueObjects,1));
%     thisTimeIndex = 1:numEntriesPerTime;
%     for i = 1:size(uniqueTimes,1)
%         theseIDs = orbitData.id(thisTimeIndex);
%         theseP = orbitData.p(thisTimeIndex,:);
%         for j = 1:size(uniqueObjects,1)
%             for k = 1:size(uniqueObjects,1)
%                 if j == k; passDistance(i,j,k) = 0; continue; end
%                 aIndex = theseIDs == uniqueObjects(j);
%                 bIndex = theseIDs == uniqueObjects(k);
%                 passDistance(i,j,k) = vecMag(theseP(aIndex,:) - theseP(bIndex,:));
%             end
%         end
%         thisTimeIndex = thisTimeIndex + numEntriesPerTime;
%     end
%     fprintf(1,' (%.1f)\n',toc);
% else
%     fprintf(1,'Skip recalculating Pass Distances\n');
% end
% 
% distanceFilePath = '.\distanceLog.txt';
% fprintf(1,'Loading distances'); tic;
% loadedDistances = importdata(distanceFilePath); loadedDistances = loadedDistances.data;
% fprintf(1,' (%.1f)\n',toc);



% dataFilePath = '.\timestepLog.txt';
function angularRateData = readTimestepLog(dataFilePath)
    vecMag = @(v) sqrt(v(:,1).^2 + v(:,2).^2);

    fid = fopen(dataFilePath,'r');
    fileText = textscan(fid,'%f\t%f\t%s\t%f\t%f\n');
    fclose(fid);
    
%     angularRateData = nan(size(fileText{1},1),5);
    angularRateData.angularRate = nan(size(fileText{1},1),1);
    angularRateData.IDA = nan(size(fileText{1},1),1);
    angularRateData.IDB = nan(size(fileText{1},1),1);
    angularRateData.d = nan(size(fileText{1},1),2);
    for i = 1:size(fileText{1},1)
%         angularRateData(i,[1,2]) = [fileText{1}(i) fileText{2}(i)];
%         secondID = str2double(fileText{3}(i));
%         if isnan(secondID)
%             secondID = -1;
%         end
%         angularRateData(i,3) = secondID;
%         angularRateData(i,[4,5]) = [fileText{4}(i) fileText{5}(i)];
        angularRateData.angularRate(i) = fileText{1}(i);
        angularRateData.IDA(i) = fileText{2}(i);
        
        secondID = str2double(fileText{3}(i));
        if isnan(secondID)
            secondID = -1;
        end
        angularRateData.IDB(i) = secondID;
        angularRateData.d(i,:) = [fileText{4}(i) fileText{5}(i)];
    end
    angularRateData.dMag = vecMag(angularRateData.d);
end
function orbitData = readSimLog(dataFilePath)
    vecMag = @(v) sqrt(v(:,1).^2 + v(:,2).^2);

    fileData = importdata(dataFilePath); fileData = fileData.data;

    keepData = fileData(:,1) ~= 0;
    
    orbitData.time = fileData(keepData,1);
    orbitData.id = fileData(keepData,2);
    
    orbitData.p = fileData(keepData,3:4);
    orbitData.pMag = vecMag(orbitData.p);
    
    orbitData.v = fileData(keepData,5:6);
    orbitData.vMag = vecMag(orbitData.v);
    
    orbitData.a = fileData(keepData,7:8);
    orbitData.aMag = vecMag(orbitData.a);
    
    orbitData.aMagNorm = orbitData.aMag(:,1).*(orbitData.pMag(:).^2);
end